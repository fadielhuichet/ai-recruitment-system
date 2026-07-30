package com.fedicode.jobservice.Controller;

import com.fedicode.jobservice.Dto.JobRequest;
import com.fedicode.jobservice.Dto.JobResponse;
import com.fedicode.jobservice.Dto.JobStatsResponse;
import com.fedicode.jobservice.Dto.RecruiterJobStatsResponse;
import com.fedicode.jobservice.Entity.JobCategory;
import com.fedicode.jobservice.Service.JobService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")

public class JobController {
    private JobService jobService;

    @PostMapping("/jobs")
    public ResponseEntity<JobResponse> createJob
            (@RequestBody JobRequest request,
             @RequestHeader("X-User-Email") String recruiterEmail,
            @RequestHeader("X-User-Roles") String role){
        if(!"RECRUITER".equalsIgnoreCase(role)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        JobResponse createJob=jobService.createJob(request,recruiterEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createJob);
    }
    @DeleteMapping("/jobs/{job_id}")
    public ResponseEntity<?> deleteJob(
            @PathVariable int job_id,
            @RequestHeader("X-User-Email") String recruiterEmail,
            @RequestHeader("X-User-Roles") String role){
        if(!"RECRUITER".equalsIgnoreCase(role)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        jobService.deleteJob(job_id,recruiterEmail);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/jobs/{jobId}")
    public ResponseEntity<?> changeJobInfo(
            @RequestBody JobRequest request,
            @PathVariable int jobId,
            @RequestHeader("X-User-Email") String recruiterEmail,
            @RequestHeader("X-User-Roles") String role){
        if(!"RECRUITER".equalsIgnoreCase(role)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        JobResponse response=jobService.changeJobInfo(request,jobId,recruiterEmail);
        return ResponseEntity.ok(response);
    }
//    @GetMapping("/jobs/{recruiter_id}")
//    public List<JobResponse> ListJob(@PathVariable int recruiter_id){
//        return jobService.getJobRecruiter(recruiter_id);
//    }
    @GetMapping("/my-jobs")
    public List<JobResponse> ListJobByCreationDate(@RequestHeader("X-User-Email")String email){
        return jobService.getJobByRecruiterIdByCreationDate(email);
    }
    @PatchMapping("/jobs/{jobId}/active")
    public ResponseEntity<?> ActivateJob(@PathVariable int jobId,@RequestHeader("X-User-Email")String email){
        String response=jobService.activeJob(jobId,email);
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/jobs/{jobId}/close")
    public ResponseEntity<?> closeJob(@PathVariable int jobId,@RequestHeader("X-User-Email")String email){
        String response=jobService.closeJob(jobId,email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/job-categories")
    public List<Map<String, String>> getCategories() {
        return Arrays.stream(JobCategory.values())
                .map(cat -> Map.of(
                        "value", cat.name(),
                        "label", cat.getDisplayName(),
                        "group", cat.getGroup()
                ))
                .toList();
    }


    @GetMapping("/jobsByCreationDateDesc")
    public Page<JobResponse> getAllJobsByCreationDateDesc(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return jobService.getJobsByCreationDateDesc(PageRequest.of(page, size));
    }


    @GetMapping("job/{id}")
    public JobResponse jobByid(@PathVariable int id){
        return jobService.getJobById(id);
    }
    @GetMapping("/jobsByCategory/{category}")
    public Page<JobResponse> getJobsByCategory(@PathVariable JobCategory category,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size){
        return jobService.getJobsByCategory(category, PageRequest.of(page,size));
    }
    @GetMapping("/search")
    public ResponseEntity<Page<JobResponse>> searchJobs(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        JobCategory jobCategory = null;
        if (category != null && !category.isBlank()) {
            try {
                jobCategory = JobCategory.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                jobCategory = null; // ignore invalid category
            }
        }
        System.out.println("Query = " + query);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(jobService.searchJobs(query, location, jobCategory, pageable));
    }

    @GetMapping("/jobs/ids/search")
    public ResponseEntity<List<Integer>> searchJobIdsByTitle(@RequestParam String title) {
        return ResponseEntity.ok(jobService.searchJobIdsByTitle(title));
    }

    @GetMapping("/activeJobs")
    public ResponseEntity<?> getActiveJobs(){
        long activeJobs=jobService.countActiveJobs();
        return ResponseEntity.status(HttpStatus.OK).body(activeJobs);
    }
    @GetMapping("/my-job-stats")
    public ResponseEntity<RecruiterJobStatsResponse> getMyJobStats(
            @RequestHeader("X-User-Email") String recruiterEmail) {
        return ResponseEntity.ok(jobService.getRecruiterJobStats(recruiterEmail));
    }


    ///(admin-side)
    @PatchMapping("/activateByAdmin/{jobId}")
    public ResponseEntity<?> activateJobByAdmin(@PathVariable int jobId, @RequestHeader("X-User-Roles")String role){
        if (!"ADMIN".equalsIgnoreCase(role)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
         jobService.activateJobByAdmin(jobId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/closeByAdmin/{jobId}")
    public ResponseEntity<?> closeJobByAdmin(@PathVariable int jobId, @RequestHeader("X-User-Roles")String role){
        if (!"ADMIN".equalsIgnoreCase(role)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        jobService.closeJobByAdmin(jobId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/deleteByAdmin/{jobId}")
    public ResponseEntity<?> deleteJobByAdmin(@PathVariable int jobId,@RequestHeader("X-User-Roles")String role){
        if(!"ADMIN".equalsIgnoreCase(role)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        jobService.deleteJobByAdmin(jobId);
        return ResponseEntity.noContent().build();
    }




    @GetMapping("/stats")
    public JobStatsResponse getStats(){
        return jobService.getStats();
    }

    @GetMapping("/jobs/admin")
    public ResponseEntity<Page<JobResponse>> getAdminJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String recruiterName,
            @RequestParam(required = false) String query) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(
                jobService.getAdminJobs(status, recruiterName, query, pageable)
        );
    }

    @GetMapping("/latest")
    public List<JobResponse> getLatestJobs() {
        return jobService.getLastThreeJobs();
    }
}
