package com.fedicode.gatewayservice.feign;

import com.fedicode.gatewayservice.model.Candidate;
import com.fedicode.gatewayservice.model.Recruiter;
import com.fedicode.gatewayservice.model.Status;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class CandidateWebClient {

    private final WebClient webClient;

    public CandidateWebClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://CANDIDATE-SERVICE").build();
    }

    public Mono<Status> getCandidateStatus(String email) {
        return webClient.get()
                .uri("/api/v1/candidate/find/{email}", email)
                .retrieve()
                .bodyToMono(Candidate.class)
                .map(Candidate::getStatus)
                .onErrorReturn(Status.ACTIVE);
    }
}
