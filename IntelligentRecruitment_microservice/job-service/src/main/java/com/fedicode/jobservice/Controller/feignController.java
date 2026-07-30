package com.fedicode.jobservice.Controller;

import com.fedicode.jobservice.Entity.Job;
import com.fedicode.jobservice.Service.JobService;
import lombok.AllArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/jobs")
@AllArgsConstructor
public class feignController {
    private JobService jobService;

    @GetMapping("/{id}")
    public Job findJobById(@PathVariable int id){
        return jobService.findJobById(id);
    }


    @GetMapping("/ids-by-recruiter")
    public ResponseEntity<List<Integer>> getJobIdsByRecruiter(
            @RequestParam String email) {
        return ResponseEntity.ok(jobService.getJobIdsByRecruiterEmail(email));
    }

    @GetMapping("/byIds")
    public List<Job> findAllByJobById(@RequestParam List<Integer> jobIds){
        return jobService.findAllByJobIds(jobIds);
    }

}
