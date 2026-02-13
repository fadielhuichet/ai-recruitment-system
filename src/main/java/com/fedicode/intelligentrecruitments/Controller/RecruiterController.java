package com.fedicode.intelligentrecruitments.Controller;

import com.fedicode.intelligentrecruitments.Entity.Job;
import com.fedicode.intelligentrecruitments.Service.ApplicationService;
import com.fedicode.intelligentrecruitments.Service.JobService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recruteur")
@AllArgsConstructor
public class RecruiterController {

    private ApplicationService applicationService;
    private JobService jobService;

    @PostMapping("/createJob")
    public Job createJob(@RequestBody Job job){
        return jobService.createJob(job);
    }
    @GetMapping("/jobs/{recruiter_id}")
    public List<Job> ListJob(@PathVariable int recruiter_id){
        return jobService.getJobRecruiter(recruiter_id);
    }
        

}
