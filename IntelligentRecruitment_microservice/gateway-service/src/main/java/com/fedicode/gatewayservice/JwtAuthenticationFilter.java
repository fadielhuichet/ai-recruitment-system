package com.fedicode.gatewayservice;


import com.fedicode.gatewayservice.feign.CandidateWebClient;
import com.fedicode.gatewayservice.feign.RecruiterWebClient;
import com.fedicode.gatewayservice.model.Status;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;


@Component
@AllArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private JwtService jwtService;
    private final RecruiterWebClient recruiterWebClient;
    private final CandidateWebClient candidateWebClient;

    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/AUTHENTICATION-SERVICE/auth/",
            "/JOB-SERVICE/api/v1/job-categories",
            "/JOB-SERVICE/api/v1/jobsByCategory",
            "/JOB-SERVICE/api/v1/jobsByCreationDateDesc",
            "/JOB-SERVICE/api/v1/search",
            "/JOB-SERVICE/api/v1/activeJobs",
            "/JOB-SERVICE/api/v1/latest",
            "/RECRUITER-SERVICE/recruiter/profile-image/"

    );
    private static final List<String> PUBLIC_POST_PATTERNS = List.of(
            "^/api/v1/jobs/[^/]+/applications$",
           "^/APPLICATION-SERVICE/api/v1/jobs/[^/]+/applications$"
    );
    private static final List<String> PUBLIC_EXACT_MATCH = List.of(
            "^/APPLICATION-SERVICE/api/v1/jobs/[^/]+/applications/count$",
//            "^/APPLICATION-SERVICE/api/v1/jobs/[^/]/applications$",
            "^/JOB-SERVICE/api/v1/job/[^/]+$"

    );

    private Boolean isPublicRoute(String path, String method) {
        // Special handling for profile image endpoints
        if (path.startsWith("/RECRUITER-SERVICE/recruiter/profile-image/")) {
            // GET requests are public (viewing images), POST requests require auth (uploading)
            return HttpMethod.GET.name().equals(method);
        }

        boolean prefixMatch = PUBLIC_PREFIXES.stream().anyMatch(path::startsWith);
        boolean postMatch = HttpMethod.POST.name().equals(method)
                && PUBLIC_POST_PATTERNS.stream().anyMatch(path::matches);
        boolean exactMatch = PUBLIC_EXACT_MATCH.stream().anyMatch(path::matches);

        return prefixMatch || postMatch || exactMatch;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().toString();

        if (isPublicRoute(path, method)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            String token = authHeader.substring(7);
            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);

            if (email == null || email.isEmpty() || jwtService.isTokenExpired(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            if ("RECRUITER".equals(role)) {
                return recruiterWebClient.getRecruiterStatus(email)
                        .flatMap(status -> {
                            if (status == Status.SUSPENDED) {
                                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                                return exchange.getResponse().setComplete();
                            }
                            return proceedWithMutatedRequest(exchange, chain, email, role); // ← was missing
                        });
            }
            if ("CANDIDATE".equals(role)) {
                return candidateWebClient.getCandidateStatus(email)
                        .flatMap(status -> {
                            if (status == Status.SUSPENDED) {
                                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                                return exchange.getResponse().setComplete();
                            }
                            return proceedWithMutatedRequest(exchange, chain, email, role); // ← was missing
                        });
            }

            return proceedWithMutatedRequest(exchange, chain, email, role);

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private Mono<Void> proceedWithMutatedRequest(ServerWebExchange exchange,
                                                 WebFilterChain chain,
                                                 String email,
                                                 String role) {
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove("X-User-Email");
                    headers.remove("X-User-Roles");
                    headers.add("X-User-Email", email);
                    headers.add("X-User-Roles", role == null ? "" : role);
                })
                .build();

        exchange.getAttributes().put("email", email);
        exchange.getAttributes().put("role", role);
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
}