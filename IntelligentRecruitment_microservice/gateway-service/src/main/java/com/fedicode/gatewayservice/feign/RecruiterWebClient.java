package com.fedicode.gatewayservice.feign;

import com.fedicode.gatewayservice.model.Recruiter;
import com.fedicode.gatewayservice.model.Status;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

// gateway-service/.../RecruiterWebClient.java
@Component
public class RecruiterWebClient {

    private final WebClient webClient;

    public RecruiterWebClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://RECRUITER-SERVICE").build();
    }

    public Mono<Status> getRecruiterStatus(String email) {
        return webClient.get()
                .uri("/recruiter/email/{email}", email)
                .retrieve()
                .bodyToMono(Recruiter.class)
                .map(Recruiter::getStatus)
                .onErrorReturn(Status.ACTIVE);
    }
}