package com.fedicode.applicationservice.feign;

import com.fedicode.applicationservice.Dto.EmailConfigDto;
import com.fedicode.applicationservice.model.Recruiter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "recruiter-service")
public interface RecruiterServiceRestClient {

    @GetMapping("/recruiter/recruiters/{id}")
    Recruiter findRecruiterById(@PathVariable int id);

    @GetMapping("/email-config/internal/{recruiterEmail}")
    EmailConfigDto getRecruiterEmailConfig(@PathVariable String recruiterEmail);

    @GetMapping("/recruiter/recruiters")
    List<Recruiter> findRecruitersByIds(@RequestParam("ids") List<Integer> ids);
}
