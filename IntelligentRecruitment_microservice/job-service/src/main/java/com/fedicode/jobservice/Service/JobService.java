package com.fedicode.jobservice.Service;

import com.fedicode.jobservice.Dto.*;
import com.fedicode.jobservice.Entity.Job;
import com.fedicode.jobservice.Entity.JobCategory;
import com.fedicode.jobservice.Entity.JobStatus;
import com.fedicode.jobservice.Repository.JobRepository;
import com.fedicode.jobservice.Repository.JobSpecification;
import com.fedicode.jobservice.feign.RecruiterServiceRestClient;
import com.fedicode.jobservice.model.Recruiter;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class JobService {

    private JobRepository jobRepository;
    private RecruiterServiceRestClient recruiterServiceRestClient;

    public Job findJobById(int id){
        return jobRepository.findById(id).orElseThrow(()->new RuntimeException("offre non trouvé"));
    }


    public JobResponse createJob(JobRequest jobRequest,String recruiterEmail){
        Recruiter recruiter=recruiterServiceRestClient.findByEmail(recruiterEmail);
        if(recruiter==null) {
            throw  new UsernameNotFoundException("recruteur non trouvé");
        }
        Job job=new Job();
        job.setCategory(jobRequest.getCategory());
        if (jobRequest.getCategory()== JobCategory.OTHER){
            if (jobRequest.getCustomCategory()==null || jobRequest.getCustomCategory().trim().isEmpty()) {
                throw new IllegalArgumentException("customCategory is required when category is OTHER");
            }
            job.setCustomCategory(jobRequest.getCustomCategory().trim());

        }else {
            job.setCustomCategory(null);
        }
        job.setTitle(jobRequest.getTitle());
        job.setDescription(jobRequest.getDescription());
        job.setCompany(jobRequest.getCompany());
        job.setLocation(jobRequest.getLocation());
        job.setApplicationLink(generateLink());
        job.setRecruiterId(recruiter.getId());
        job.setRecruiter(recruiter);
        job.setCreatedAt(LocalDateTime.now());
        job.setStatus(JobStatus.ACTIVE);
        jobRepository.save(job);
        return JobResponse.builder()
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .createdAt(LocalDateTime.now())
                .recruiterEmail(recruiter.getEmail())
                .recruiterCompany(recruiter.getCompanyName())
                .build();


    }

    private String generateLink() {
        return UUID.randomUUID().toString();
    }

    public List<JobResponse> getJobRecruiter(int id){
        List<Job> jobs =jobRepository.findByRecruiterId(id);
        return jobs.stream()
                .map(job -> toJobResponse(job, null))
                .collect(Collectors.toList());

    }

    public List<JobResponse> getJobByRecruiterIdByCreationDate(String recruiterEmail){
        Recruiter recruiter=recruiterServiceRestClient.findByEmail(recruiterEmail);
        if(recruiter==null){
            throw new RuntimeException("recruiter not found");
        }
        List<Job> jobs=jobRepository.findByRecruiterIdOrderByCreatedAtDesc(recruiter.getId());
        return jobs.stream()
                .map(job -> toJobResponse(job, recruiter))
                .collect(Collectors.toList());
    }

    public void deleteJob(int job_id,String recruiterEmail){
        Job job=jobRepository.findById(job_id)
                .orElseThrow(()->new RuntimeException("Job not found"));
        assertRecruiterOwnsJob(job, recruiterEmail);
        jobRepository.deleteById(job_id);
    }

    @Transactional
    public JobResponse changeJobInfo(JobRequest request, int jobId, String recruiterEmail) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job does not exist"));

        Recruiter recruiter = assertRecruiterOwnsJob(job, recruiterEmail);

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            job.setTitle(request.getTitle());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            job.setDescription(request.getDescription());
        }
        if (request.getCompany() != null && !request.getCompany().isBlank()) {
            job.setCompany(request.getCompany());
        }
        if (request.getLocation() != null && !request.getLocation().isBlank()) {
            job.setLocation(request.getLocation());
        }
        if (request.getCategory() != null) {
            job.setCategory(request.getCategory());
            if (request.getCategory() == JobCategory.OTHER) {
                if (request.getCustomCategory() != null && !request.getCustomCategory().isBlank()) {
                    job.setCustomCategory(request.getCustomCategory());
                }
            } else {
                job.setCustomCategory(null); // clear if no longer OTHER
            }
        }

        jobRepository.save(job);
        return toJobResponse(job, recruiter);
    }

    private Recruiter assertRecruiterOwnsJob(Job job, String recruiterEmail) {
        Recruiter recruiter = recruiterServiceRestClient.findByEmail(recruiterEmail);
        if (recruiter == null) {
            throw new UsernameNotFoundException("Recruiter does not exist");
        }
        if (job.getRecruiterId() != recruiter.getId()) {
            throw new RuntimeException("Recruiter not authorized ");
        }
        return recruiter;
    }

    private JobResponse toJobResponse(Job job, Recruiter fallbackRecruiter) {
        Recruiter recruiter = job.getRecruiter() != null ? job.getRecruiter() : fallbackRecruiter;
        JobCategoryDto categoryDto = null;
        if (job.getCategory() != null) {
            categoryDto = new JobCategoryDto(
                    job.getCategory().name(),              // "SOFTWARE_DEVELOPMENT"
                    job.getCategory().getDisplayName(),    // "Software Development"
                    job.getCategory().getGroup()           // "Tech & Software Engineering"
            );
        }
        JobResponse.JobResponseBuilder builder = JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .category(categoryDto)
                .createdAt(job.getCreatedAt())
                .status(job.getStatus())
                .customCategory(job.getCustomCategory());

        if (recruiter != null) {
            builder.recruiterEmail(recruiter.getEmail())
                    .recruiterCompany(recruiter.getCompanyName())
                    .recruiterFirstName(recruiter.getFirstName())
                    .recruiterLastName(recruiter.getLastName());
        }

        return builder.build();
    }

    public String activeJob(int jobId,String recruiterEmail){
        Job job=jobRepository.findById(jobId)
                .orElseThrow(()->new RuntimeException("job not found"));
        assertRecruiterOwnsJob(job,recruiterEmail);
        if (job.getStatus()== JobStatus.ACTIVE){
            throw new IllegalArgumentException("job already closed");
        }
        job.setStatus(JobStatus.ACTIVE);
        jobRepository.save(job);
        return "job activated successfully";
    }

    public String closeJob(int jobId,String recruiterEmail){
        Job job=jobRepository.findById(jobId)
                .orElseThrow(()->new RuntimeException("job not found"));
        assertRecruiterOwnsJob(job,recruiterEmail);
        if (job.getStatus()== JobStatus.CLOSED){
            throw new IllegalArgumentException("job already closed");
        }
        job.setStatus(JobStatus.CLOSED);
        jobRepository.save(job);
        return "job closed successfully";
    }

    public Page<JobResponse> getAdminJobs(
            String status,
            String recruiterName,
            String query,
            Pageable pageable) {

        // resolve recruiter name to IDs via recruiter-service
        List<Integer> recruiterIds = null;
        if (recruiterName != null && !recruiterName.isBlank()) {
            recruiterIds = recruiterServiceRestClient
                    .findRecruitersByName(recruiterName)
                    .stream()
                    .map(Recruiter::getId)
                    .toList();

            // name provided but no recruiter found → return empty page
            if (recruiterIds.isEmpty()) {
                return Page.empty(pageable);
            }
        }

        Page<Job> jobs = jobRepository.findAll(
                JobSpecification.withFilters2(status, recruiterIds, query),
                pageable
        );

        // batch load recruiters for response building
        List<Integer> allRecruiterIds = jobs.getContent().stream()
                .map(Job::getRecruiterId)
                .distinct()
                .toList();

        Map<Integer, Recruiter> recruiterMap = allRecruiterIds.isEmpty()
                ? Map.of()
                : recruiterServiceRestClient.findRecruitersByIds(allRecruiterIds)
                .stream()
                .collect(Collectors.toMap(Recruiter::getId, r -> r));

        return jobs.map(job -> {
            JobCategoryDto categoryDto = null;
            if (job.getCategory() != null) {
                categoryDto = new JobCategoryDto(
                        job.getCategory().name(),              // "SOFTWARE_DEVELOPMENT"
                        job.getCategory().getDisplayName(),    // "Software Development"
                        job.getCategory().getGroup()           // "Tech & Software Engineering"
                );
            }
            Recruiter recruiter = recruiterMap.get(job.getRecruiterId());
            return JobResponse.builder()
                    .id(job.getId())
                    .title(job.getTitle())
                    .description(job.getDescription())
                    .company(job.getCompany())
                    .location(job.getLocation())
                    .status(job.getStatus())
                    .category(categoryDto)
                    .customCategory(job.getCustomCategory())
                    .createdAt(job.getCreatedAt())
                    .recruiterId(job.getRecruiterId())
                    .recruiterFirstName(recruiter != null ? recruiter.getFirstName() : "Unknown")
                    .recruiterLastName(recruiter != null ? recruiter.getLastName() : "Unknown")
                    .recruiterEmail(recruiter != null ? recruiter.getEmail() : "Unknown")
                    .recruiterPhone(recruiter != null ? recruiter.getPhone() : "Unknown")
                    .build();
        });
    }

    public Page<JobResponse> getJobsByCreationDateDesc(Pageable pageable){
        Page<Job> jobs= jobRepository.getAllByOrderByCreatedAtDesc(pageable);
        List<Integer> recruiterIds = jobs.getContent()
                .stream()
                .map(Job::getRecruiterId)
                .distinct()
                .toList();
        List<Recruiter> recruiters =
                recruiterServiceRestClient.findRecruitersByIds(recruiterIds);
        Map<Integer, Recruiter> recruiterMap = recruiters.stream()
                .collect(Collectors.toMap(Recruiter::getId, r -> r));


        return jobs.map(job -> {
            Recruiter recruiter = recruiterMap.get(job.getRecruiterId());

            return JobResponse.builder()
                    .id(job.getId())
                    .title(job.getTitle())
                    .description(job.getDescription())
                    .createdAt(job.getCreatedAt())
                    .location(job.getLocation())
                    .company(job.getCompany())
                    .status(job.getStatus())
                    .recruiterId(job.getRecruiterId())
                    .recruiterFirstName(
                            recruiter != null
                                    ? recruiter.getFirstName()
                                    : "Unknown"
                    )
                    .recruiterLastName(
                            recruiter != null
                            ? recruiter.getLastName()
                                    :"unknown"
                    )
                    .recruiterEmail(
                            recruiter != null
                                    ? recruiter.getEmail()
                                    : "Unknown"
                    )
                    .recruiterPhone(
                            recruiter != null
                                    ? recruiter.getPhone()
                                    : "Unknown"
                    )
                    .build();
        });
    }
    public long countActiveJobs(){
        return jobRepository.countActiveJobs();
    }

    public JobResponse getJobById(int id){
        Job job=jobRepository.findById(id)
                .orElseThrow(()->new RuntimeException("job not found"));
        JobCategoryDto categoryDto = null;
        if (job.getCategory() != null) {
            categoryDto = new JobCategoryDto(
                    job.getCategory().name(),              // "SOFTWARE_DEVELOPMENT"
                    job.getCategory().getDisplayName(),    // "Software Development"
                    job.getCategory().getGroup()           // "Tech & Software Engineering"
            );
        }
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .createdAt(job.getCreatedAt())
                .description(job.getDescription())
                .location(job.getLocation())
                .company(job.getCompany())
                .category(categoryDto)
                .status(job.getStatus())
                .build();
    }

    public Page<JobResponse> getJobsByCategory(JobCategory category, Pageable pageable){
        Page<Job> jobs= jobRepository.findByCategoryOrderByCreatedAtDesc(category,pageable);
        return jobs.map(job -> JobResponse.builder()
                        .id(job.getId())
                        .title(job.getTitle())
                        .description(job.getDescription())
                        .createdAt(job.getCreatedAt())
                        .location(job.getLocation())
                        .company(job.getCompany())
                        .build());
    }

   public Page<JobResponse> searchJobs(
           String title,
           String location,
           JobCategory category,
           Pageable pageable){
        return jobRepository.findAll(
                JobSpecification.withFilters(title,location,category),
                pageable
        ).map(job -> JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .createdAt(job.getCreatedAt())
                .location(job.getLocation())
                .company(job.getCompany())
                .build());
   }

   public List<Integer> searchJobIdsByTitle(String title){
        if (title == null || title.isBlank()) {
            return List.of();
        }
        return jobRepository.findAll(JobSpecification.withFilters(title,  null,null))
                .stream()
                .map(Job::getId)
                .toList();
   }

    public JobStatsResponse getStats() {
        long total    = jobRepository.count();
        long active   = jobRepository.countByStatus(JobStatus.ACTIVE);
        long closed   = jobRepository.countByStatus(JobStatus.CLOSED);
        long thisMonth = jobRepository.countByCreatedAtAfter(
                LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
        );

        return JobStatsResponse.builder()
                .total(total)
                .active(active)
                .closed(closed)
                .thisMonth(thisMonth)
                .build();
    }

    public void activateJobByAdmin(int jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found"));

        if (job.getStatus() == JobStatus.ACTIVE) {
            throw new IllegalStateException("Job already active");
        }

        job.setStatus(JobStatus.ACTIVE);
        jobRepository.save(job);
    }

    public void closeJobByAdmin(int jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found"));

        if (job.getStatus() == JobStatus.CLOSED) {
            throw new IllegalStateException("Job already close");
        }

        job.setStatus(JobStatus.CLOSED);
        jobRepository.save(job);
    }

    public void deleteJobByAdmin(int jobId) {
        Job job=jobRepository.findById(jobId)
                .orElseThrow(()->new RuntimeException("Job not found"));
        jobRepository.deleteById(jobId);
    }


    public RecruiterJobStatsResponse getRecruiterJobStats(String recruiterEmail){
        Recruiter recruiter=recruiterServiceRestClient.findByEmail(recruiterEmail);
        if (recruiter == null) {
            throw new UsernameNotFoundException("Recruiter not found");
        }
        int recruiterId=recruiter.getId();

        LocalDateTime firstOfMonth = LocalDateTime.now()
                .withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        return RecruiterJobStatsResponse.builder()
                .total(jobRepository.countByRecruiterId(recruiterId))
                .active(jobRepository.countByRecruiterIdAndStatus(recruiterId, JobStatus.ACTIVE))
                .closed(jobRepository.countByRecruiterIdAndStatus(recruiterId, JobStatus.CLOSED))
                .thisMonth(jobRepository.countByRecruiterIdAndCreatedAtAfter(recruiterId, firstOfMonth))
                .build();
    }

    public List<Integer> getJobIdsByRecruiterEmail(String email) {
        Recruiter recruiter = recruiterServiceRestClient.findByEmail(email);
        if (recruiter == null) return List.of();
        return jobRepository.findByRecruiterId(recruiter.getId())
                .stream()
                .map(Job::getId)
                .toList();
    }

    public List<JobResponse> getLastThreeJobs() {

        List<Job> jobs = jobRepository.findTop3ByOrderByCreatedAtDesc();

        return jobs.stream().map(job -> {

            JobCategoryDto categoryDto = null;

            if (job.getCategory() != null) {
                categoryDto = new JobCategoryDto(
                        job.getCategory().name(),
                        job.getCategory().getDisplayName(),
                        job.getCategory().getGroup()
                );
            }

            return JobResponse.builder()
                    .id(job.getId())
                    .title(job.getTitle())
                    .description(job.getDescription())
                    .createdAt(job.getCreatedAt())
                    .location(job.getLocation())
                    .company(job.getCompany())
                    .category(categoryDto)
                    .build();

        }).toList();
    }


    public List<Job> findAllByJobIds(List<Integer> jobIds) {
        return jobRepository.findAllByIdIn(jobIds);
    }
}
