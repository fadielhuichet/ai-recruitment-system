# Quick Start Guide - Centralized JWT Setup

## ✅ What Was Changed

1. **Gateway Service** - Now validates all JWT tokens
   - `JwtService.java` - Token validation logic
   - `JwtAuthenticationFilter.java` - Intercepts all requests
   - `SecurityConfig.java` - Routes config

2. **Internal Services** - Simplified security
   - Removed JWT filters (now at gateway)
   - Removed role-based authorization (gateway routes only valid tokens)
   - Allow all requests (security done upstream)

## 🚀 How to Run

### Option 1: Docker Compose (Recommended)

```bash
cd C:\Users\WSI\Documents\3LM\DevWeb\ (Spring\ Boot)\Projects\IntelligentRecruitment_microservice

# Make sure .env has JWT_SECRET
docker compose up -d --build
```

Services will start in this order:
1. `config-server` (8888) - Configuration
2. `discovery-service` (8761) - Eureka service registry
3. Database & other services
4. `gateway-service` (8086) - **ONLY PUBLIC ENDPOINT**

### Option 2: Local Development

```bash
# Terminal 1: Config Server
cd config-server
.\mvnw.cmd spring-boot:run

# Terminal 2: Discovery Service
cd discovery-service
.\mvnw.cmd spring-boot:run

# Terminal 3: Gateway
cd gateway-service
$env:CONFIG_SERVER_URL="http://localhost:8888"
$env:JWT_SECRET="ZGV2LXNlY3JldC1jaGFuZ2UtbWUtdGhpcy1pcy1iYXNlNjQ="
.\mvnw.cmd spring-boot:run

# Terminal 4+: Business services
cd job-service
$env:CONFIG_SERVER_URL="http://localhost:8888"
.\mvnw.cmd spring-boot:run
```

## 🔐 Authentication Flow

### Step 1: Get a Token

```bash
curl -X POST http://localhost:8086/AUTHENTICATION-SERVICE/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "recruiter@test.com",
    "password": "Password123!",
    "role": "RECRUITER"
  }'
```

**Response:**
```json
{
  "id": 1,
  "email": "recruiter@test.com",
  "role": "RECRUITER",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiUkVDUlVJVEVSIiwic3ViIjoicmVjcnVpdGVyQHRlc3QuY29tIiwiaWF0IjoxNjc2MzIxNDAwLCJleHAiOjE2NzYzNjQ2MDB9.xyz..."
}
```

### Step 2: Use Token to Access Services

```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Create a job
curl -X POST http://localhost:8086/JOB-SERVICE/recruiter/jobs/createJob \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Senior Java Developer",
    "description": "Build great things",
    "company": "TechCorp",
    "location": "Remote",
    "applicationLink": "https://careers.example.com/job/123"
  }'

# Get all jobs
curl -X GET http://localhost:8086/JOB-SERVICE/recruiter/jobs \
  -H "Authorization: Bearer $TOKEN"
```

### Step 3: Try Without Token (Should Fail)

```bash
curl -X GET http://localhost:8086/JOB-SERVICE/recruiter/jobs
# Response: 401 Unauthorized
```

## 📋 API Endpoints

### Public (No JWT Required)
- `POST /AUTHENTICATION-SERVICE/auth/register` - Register user
- `POST /AUTHENTICATION-SERVICE/auth/login` - Login user
- `POST /RECRUITER-SERVICE/recruiter/create` - Create recruiter

### Protected (JWT Required)
- `POST /JOB-SERVICE/recruiter/jobs/createJob`
- `GET /JOB-SERVICE/recruiter/jobs`
- `POST /APPLICATION-SERVICE/recruiter/application/**`
- Any endpoint on business services

## 🔍 Verification

### Check Gateway is Working
```bash
curl http://localhost:8086/actuator/health
# Should return 200 OK
```

### Check Discovery (Eureka)
```bash
curl http://localhost:8761/eureka/apps
# Should show registered services
```

### Check if Service is Reachable
```bash
# This will fail (no token)
curl http://localhost:8086/JOB-SERVICE/recruiter/jobs

# This will succeed (with token)
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8086/JOB-SERVICE/recruiter/jobs
```

## 🛑 Common Issues

| Issue | Solution |
|-------|----------|
| "401 Unauthorized" on valid token | Check JWT_SECRET matches in gateway & config-server |
| "No route found" | Verify service is running & registered in Eureka |
| Gateway won't start | Check port 8086 is free; ensure config-server is running first |
| Feign calls fail | Services must use container names in Docker (e.g., `http://job-service:8084`) |

## 📊 Architecture Summary

```
┌─────────────────────────────────────────────────┐
│  Client (Postman, Frontend, Mobile)             │
└────────────────────┬────────────────────────────┘
                     │ HTTP Requests
                     ↓
    ┌────────────────────────────────────┐
    │  GATEWAY SERVICE (Port 8086)       │
    │  - JwtAuthenticationFilter         │
    │  - Route Discovery (Eureka)        │
    │  PUBLIC FACING ONLY                │
    └────────────────────────────────────┘
                     │
      ┌──────────────┼──────────────┐
      ↓              ↓              ↓
  ┌─────────┐  ┌─────────┐  ┌─────────────┐
  │ Auth    │  │  Job    │  │ Recruiter   │
  │ Service │  │ Service │  │  Service    │
  │(8083)   │  │(8084)   │  │  (8082)     │
  └─────────┘  └─────────┘  └─────────────┘
      ↓              ↓              ↓
  ┌────────────────────────────────────────┐
  │  PostgreSQL (Single DB or Multi-DB)    │
  └────────────────────────────────────────┘

All services communicate via:
- Service discovery (Eureka)
- Internal Docker network
```

## ✨ Benefits of This Setup

✅ **Single Point of JWT Validation** - Easier to maintain  
✅ **Services Are Isolated** - Can't be accessed directly from outside  
✅ **Simplified Service Security** - Less code duplication  
✅ **Better Performance** - No redundant JWT validation  
✅ **Easier Testing** - Can test gateway separately from services  

## 🎯 Next Steps

1. Test the flow with Postman
2. Deploy to Docker Compose
3. Add rate limiting to gateway (production)
4. Implement refresh tokens
5. Add monitoring/alerting

