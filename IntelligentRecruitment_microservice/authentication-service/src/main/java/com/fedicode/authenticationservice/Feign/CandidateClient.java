package com.fedicode.authenticationservice.Feign;


import com.fedicode.authenticationservice.model.Candidate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@FeignClient(name = "candidate-service")
public interface CandidateClient {
    @GetMapping("/api/v1/candidate/exist/{email}")
    Boolean existsByEmail(@PathVariable String email);

    @GetMapping("/api/v1/candidate/find/{email}")
    Optional<Candidate> findByEmail(@PathVariable String email);

    @PostMapping("/api/v1/create")
    Candidate saveCandidate (@RequestBody Candidate application);

}
