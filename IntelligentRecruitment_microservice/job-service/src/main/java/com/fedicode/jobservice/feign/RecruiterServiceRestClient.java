package com.fedicode.jobservice.feign;

import com.fedicode.jobservice.model.Recruiter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "recruiter-service")
public interface RecruiterServiceRestClient {
    @GetMapping("/recruiter/email/{email}")
    Recruiter findByEmail(@PathVariable String email);
    @GetMapping("/recruiter/recruiters/{id}")
    Recruiter findRecruiterById(@PathVariable int id);

    @GetMapping("/recruiter/recruiters")
    List<Recruiter> findRecruitersByIds(@RequestParam("ids") List<Integer> ids);
    @GetMapping("/recruiter/search/byName")
    List<Recruiter> findRecruitersByName(@RequestParam String name);
}
