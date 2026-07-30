package com.fedicode.applicationservice.Controller;

import com.fedicode.applicationservice.Dto.*;
import com.fedicode.applicationservice.Entity.Application;
import com.fedicode.applicationservice.Service.ApplicationService;
import com.fedicode.applicationservice.Service.EmailService;
import com.fedicode.applicationservice.Service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class ApplicationController {
    private final EmailService emailService;
    private final FileService fileService;
    private ApplicationService applicationService;

    @PostMapping("/jobs/{job_id}/applications")
    public ResponseEntity<?> createApplication(@PathVariable int job_id,@RequestHeader("X-User-Email") String email){
        applicationService.createApplication(job_id,email);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "candidature envoyer avec succés"));

    }
//    @PreAuthorize("hasRole('RECRUITER')")
    @DeleteMapping("/applications/{appId}")
    public ResponseEntity<?> deleteApplication(@PathVariable int appId,@RequestHeader("X-User-Email") String recruiterEmail){
        applicationService.deleteApplication(appId,recruiterEmail);
        return ResponseEntity.noContent().build();

    }
    @GetMapping("/applications/{appId}")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable int appId, @RequestHeader("X-User-Email") String recruiterEmail){
        ApplicationResponse response=applicationService.getApplicationById(appId);
        return ResponseEntity.ok(response);
    }
//    @GetMapping("/jobs/{job_id}/applications")
//    public List<ApplicationResponse> listApplication(@PathVariable int job_id){
//        return applicationService.getAllApplications(job_id);
//    }
    @GetMapping(value = "/jobs/{job_id}/applications" ,params = "sort=createdAt,desc")
    public List<ApplicationResponse> applicationsSortedByCreationDate(@PathVariable int job_id){
        return applicationService.getApplicationByCreationDateDesc(job_id);
    }
    @GetMapping(value = "/jobs/{job_id}/applications", params = "sort=score,desc")
    public List<ApplicationResponse> applicationsSortedByScore(@PathVariable int job_id){
        return applicationService.getApplicationByScoreDesc(job_id);
    }

    @GetMapping("/jobs/{job_id}/applications/search")
    public ResponseEntity<Page<ApplicationResponse>> searchRecruiterApplications(
            @PathVariable int job_id,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(applicationService.searchRecruiterApplications(query, job_id, pageable));
    }

    @PostMapping("/accept")
    public ResponseEntity<String> bulkAccept(@RequestBody List<Integer> applicationIds){
        applicationIds.forEach(emailService::sendAcceptedEmail);
        return ResponseEntity.accepted().body("Sending acceptance emails to "
                + applicationIds.size() + " candidates");
    }

    @PostMapping("/refuse")
    public ResponseEntity<String> bulkRefuse(@RequestBody List<Integer> applicationIds){
        applicationIds.forEach(emailService::sendRefusedEmail);
        return ResponseEntity.accepted().body("Sending refusal emails to "
                + applicationIds.size() + " candidates");
    }
    @GetMapping("/jobs/{job_id}/applications/count")
    public long getApplicantCount(@PathVariable int job_id) {
        return applicationService.getApplicantCount(job_id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApplicationByAdmin(@PathVariable int id,@RequestHeader("X-User-Roles")String role){

        applicationService.deleteApplicationByAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/applicationsByStatus")
    public ResponseEntity<Page<ApplicationResponse>> getApplicationByStatus(
            @RequestParam int id,
            @RequestParam Application.ApplicationStatus status,
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10")int size
    ){
        Pageable pageable= PageRequest.of(page,size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(applicationService.getApplicationsByStatus(id,status,pageable));
    }

    @PatchMapping("/updateStatus")
    public ResponseEntity<?> updateStatus(@RequestParam int id,@RequestParam Application.ApplicationStatus status){
        applicationService.updateStatus(id,status);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/admin")
    public ResponseEntity<AdminApplicationStatsResponse> getAdminStats() {
        return ResponseEntity.ok(applicationService.getAdminStats());
    }

    @GetMapping("/stats/recruiter")
    public ResponseEntity<RecruiterApplicationStatsResponse> getRecruiterStats(
            @RequestHeader("X-User-Email") String recruiterEmail) {
        return ResponseEntity.ok(applicationService.getRecruiterStats(recruiterEmail));
    }

    @GetMapping("/applications")
    public ResponseEntity<Page<AdminApplicationResponse>> getAllApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(applicationService.getAllApplications(pageable));
    }

    @GetMapping("/applications/search")
    public ResponseEntity<Page<AdminApplicationResponse>> searchAdminApplications(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Application.ApplicationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(applicationService.searchAdminApplications(query, status, pageable));
    }

    /// feign-client

    @GetMapping("/application/exist/{email}")
    public Boolean existByEmail(@PathVariable String email){
        return applicationService.existByEmail(email);
    }

    @GetMapping("/application/{email}")
    public Optional<Application> findByEmail(@PathVariable String email){
        return applicationService.findByEmail(email);
    }

    @PostMapping("/create")
    public Application createApp(@RequestBody Application application) {
        return applicationService.createApp(application);
    }

    @GetMapping("/{candidateId}/applications")
    public ResponseEntity<List<CandidateApplicationResponse>>
    getApplicationsByCandidate(@PathVariable int candidateId) {

        return ResponseEntity.ok(
                applicationService.getApplicationsByCandidate(candidateId)
        );
    }

    @GetMapping("/{candidateId}/stats")
    public ResponseEntity<CandidateApplicationStatsResponse>
    getCandidateStats(@PathVariable int candidateId) {

        return ResponseEntity.ok(
                applicationService.getCandidateStats(candidateId)
        );
    }

    @GetMapping("/download/{applicationId}")
    public ResponseEntity<Resource> downloadCv(@PathVariable int applicationId) {
        return fileService.getPdfFromFolder(applicationId);
    }
}
