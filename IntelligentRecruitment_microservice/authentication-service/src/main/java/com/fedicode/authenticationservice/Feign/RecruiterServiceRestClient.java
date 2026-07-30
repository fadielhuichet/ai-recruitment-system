package com.fedicode.authenticationservice.Feign;

import com.fedicode.authenticationservice.model.Recruiter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@FeignClient(name = "recruiter-service")
public interface RecruiterServiceRestClient {
    @PostMapping("/recruiter/create")
    Recruiter create (@RequestBody Recruiter recruiter);

    @GetMapping("/recruiter/exists/{recruiterEmail}")
    Boolean existsByEmail(@PathVariable String recruiterEmail);

    @GetMapping("/recruiter/email/{email}")
   Optional <Recruiter> findByEmail(@PathVariable String email);
}
