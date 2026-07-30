# Repository Guidelines

## Project Structure & Module Organization

This workspace contains two independent Git repositories:

- **`IntelligentRecruitment_microservice/`** — Spring Boot 4.0.3 + Spring Cloud 2025.1.0 backend (Java 17, Maven multi-module)
- **`IntelligentRecruitment-UI/`** — Angular 21 standalone-component frontend (TypeScript 5.9, Tailwind CSS 4)

### Backend microservices (ports)

| Service | Port | Role |
|---|---|---|
| `config-server` | 8888 | Centralized config (native profile, reads `classpath:/configurations/*.yml`) |
| `discovery-service` | 8761 | Eureka server |
| `gateway-service` | 8086 | **Only public entry point**; owns JWT validation |
| `authentication-service` | 8083 | Login/register, token issuance |
| `admin-service` | 8081 | Admin operations |
| `recruiter-service` | 8082 | Recruiter profiles |
| `job-service` | 8084 | Job listings |
| `application-service` | 8085 | CV upload, AI scoring (Spring AI Ollama + PDFBox) |

JWT validation is centralized in `gateway-service/.../JwtAuthenticationFilter.java`. Internal services use `anyRequest().permitAll()` + `STATELESS` sessions — they rely entirely on the gateway for auth.

Gateway routes use **uppercase service-name prefixes**: `/JOB-SERVICE/**`, `/AUTHENTICATION-SERVICE/**`, etc.

Service-to-service calls use OpenFeign with Eureka service names (never hardcoded hosts). Controllers receive identity via gateway-injected headers `X-User-Email` and `X-User-Roles`.

### Frontend architecture

Angular 21 with **standalone components** (no NgModules). Application config in `src/app/app.config.ts` registers the functional `httpTokenInterceptor` globally. Route protection uses functional guard `recruiterGuard` which decodes the JWT payload from `localStorage` and checks `role === 'RECRUITER'`. All HTTP calls target the gateway at `http://localhost:8086` with the uppercase service prefix.

Structure under `src/app/`:
- `Pages/` — page-level components (public + `Recruiter/` subtree)
- `Shared/` — header, footer, sidebar layouts
- `services/` — `services/` (per-backend-service), `guards/`, `interceptor/`
- `models/` — `Dto/`, `Entity/`, `Enum/`

## Build, Test, and Development Commands

### Backend (run from a service directory or repo root)

```bash
# Build and run a single service
./mvnw spring-boot:run

# Run tests for a single service
./mvnw test

# Full stack via Docker Compose (from IntelligentRecruitment_microservice/)
docker compose up -d --build

# Tear down
docker compose down
```

Start order: `config-server` → `discovery-service` → other services. PostgreSQL is exposed on host port **5433**.

### Frontend (from `IntelligentRecruitment-UI/`)

```bash
npm start          # ng serve (dev server)
npm run build      # production build
npm run watch      # incremental dev build
npm test           # ng test (Vitest)
```

## Coding Style & Naming Conventions

### Backend
- Lombok (`@Data`, `@Builder`, etc.) — no manual getters/setters
- Package layout per module: `Config`, `Service`, `Controller`, `feign`, `model`/`dto`
- Secrets via environment variables only — never hardcode `JWT_SECRET`, DB passwords, etc.
- Per-service config lives in `config-server/src/main/resources/configurations/<service-name>.yml`

### Frontend
- TypeScript **strict mode** + `noImplicitOverride`, `noImplicitReturns`, `strictTemplates`, `strictInjectionParameters`
- Prettier for formatting (configured in `devDependencies`)
- Functional interceptors and guards (not class-based)
- Use `inject()` inside functional guards/interceptors — **not** `Inject()` (constructor injection)
- `TokenService` is the single source of truth for reading/writing the JWT from `localStorage`
- Component files: `<name>.ts`, `<name>.html`, `<name>.css`, `<name>.spec.ts` — no `.component.` infix

## Testing Guidelines

**Backend:** `./mvnw test` inside a service module. Uses Spring Boot Test + Spring Security Test.

**Frontend:** `npm test` (Vitest + jsdom). Spec files are co-located with their component/service.

## Architecture Constraints

- Never bypass the gateway from the frontend — all API calls must go through `localhost:8086`.
- If you add or rename an endpoint, update the matching `config-server` YAML and any `docker-compose.yml` environment references.
- If you change gateway auth logic, do so only in `gateway-service/.../JwtAuthenticationFilter.java`.
- When modifying a Feign contract, update both the client interface and the provider controller together.
