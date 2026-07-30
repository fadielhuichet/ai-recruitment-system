package com.fedicode.applicationservice.feign;

import com.fedicode.applicationservice.model.Candidate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "candidate-service")
public interface CandidateClient {
    @GetMapping("/api/v1/candidate/find/{email}")
    Optional<Candidate> findByEmail(@PathVariable String email);
}
