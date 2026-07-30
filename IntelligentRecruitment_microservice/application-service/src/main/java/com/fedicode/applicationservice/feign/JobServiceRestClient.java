package com.fedicode.applicationservice.feign;

import com.fedicode.applicationservice.model.Job;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "job-service")
public interface JobServiceRestClient {
    @GetMapping("/jobs/{id}")
    Job findJobById(@PathVariable int id);

    @GetMapping("/jobs/ids-by-recruiter")
    List<Integer> getJobIdsByRecruiterEmail(@RequestParam("email") String email);

    @GetMapping("/jobs/byIds")
    List<Job> findAllByJobById(@RequestParam List<Integer> jobIds);

    @GetMapping("/api/v1/jobs/ids/search")
    List<Integer> findJobIdsByTitle(@RequestParam("title") String title);
}
