package com.fedicode.applicationservice.Service;

import com.fedicode.applicationservice.CustomException.EmailAlreadyUsed;
import com.fedicode.applicationservice.Dto.*;
import com.fedicode.applicationservice.Entity.Application;
import com.fedicode.applicationservice.feign.CandidateClient;
import com.fedicode.applicationservice.feign.JobServiceRestClient;
import com.fedicode.applicationservice.feign.RecruiterServiceRestClient;
import com.fedicode.applicationservice.model.Candidate;
import com.fedicode.applicationservice.model.Job;
import com.fedicode.applicationservice.Repository.ApplicationRepository;

import com.fedicode.applicationservice.model.JobStatus;
import com.fedicode.applicationservice.model.Recruiter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

@Service
@AllArgsConstructor
@Slf4j
public class ApplicationService {

    private ApplicationRepository applicationRepository;
    private JobServiceRestClient jobServiceRestClient;
    private RecruiterServiceRestClient recruiterClient;
    private CandidateClient candidateClient;
    private FileService fileService;
    private LlmService llmService;

    public void createApplication(int job_id, String candidateEmail) {
        Job job = jobServiceRestClient.findJobById(job_id);
        Candidate candidate = candidateClient.findByEmail(candidateEmail)
                .orElseThrow(() -> new RuntimeException("Email does not exist"));

        if (job.getStatus() == JobStatus.CLOSED) {
            throw new RuntimeException("job offer is closed");
        }
        if (applicationRepository.existsByJobIdAndCandidateEmail(job_id, candidateEmail)) {
            throw new EmailAlreadyUsed("You can only apply once.");
        }
        String cvText = fileService.extractText(candidate.getCvFilePath());
        Application application = Application.builder()
                .job(job)
                .jobId(job_id)
                .candidate(candidate)
                .candidateId(candidate.getId())
                .candidateFirstName(candidate.getFirstName())
                .candidateLastName(candidate.getLastName())
                .candidateEmail(candidate.getEmail())
                .candidatePhone(candidate.getPhone())
                .dateOfBirth(candidate.getDateOfBirth())
                .cvFilePath(candidate.getCvFilePath())
                .cvText(cvText)
                .llmScore(null)
                .llmAnalysis(null)
                .status(Application.ApplicationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        Application savedApplication = applicationRepository.save(application);
        llmService.analysAsync(savedApplication.getId(), job.getDescription(), cvText);

    }

    public List<ApplicationResponse> getAllApplications(int id) {
        List<Application> applications = applicationRepository.findByJobId(id);
        return applications.stream()
                .map(application -> ApplicationResponse.builder()
                        .id(application.getId())
                        .firstName(application.getCandidateFirstName())
                        .lastName(application.getCandidateLastName())
                        .phone(application.getCandidatePhone())
                        .email(application.getCandidateEmail())
                        .llmScore(application.getLlmScore())
                        .status(application.getStatus())
                        .createdAt(application.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public List<ApplicationResponse> getApplicationByScoreDesc(int id) {
        List<Application> applications = applicationRepository.findByJobIdOrderByLlmScoreDesc(id);
        return applications.stream()
                .map(application -> ApplicationResponse.builder()
                        .id(application.getId())
                        .firstName(application.getCandidateFirstName())
                        .lastName(application.getCandidateLastName())
                        .phone(application.getCandidatePhone())
                        .email(application.getCandidateEmail())
                        .llmScore(application.getLlmScore())
                        .status(application.getStatus())
                        .createdAt(application.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public List<ApplicationResponse> getApplicationByCreationDateDesc(int id) {
        List<Application> applications = applicationRepository.findByJobIdOrderByCreatedAtDesc(id);
        return applications.stream()
                .map(application -> ApplicationResponse.builder()
                        .id(application.getId())
                        .firstName(application.getCandidateFirstName())
                        .lastName(application.getCandidateLastName())
                        .phone(application.getCandidatePhone())
                        .email(application.getCandidateEmail())
                        .llmScore(application.getLlmScore())
                        .status(application.getStatus())
                        .createdAt(application.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteApplication(int appId, String recruiterEmail) {
        Application application = applicationRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("candidature non trouvée"));

        Job job = jobServiceRestClient.findJobById(application.getJobId());
        Recruiter recruiter = recruiterClient.findRecruiterById(job.getRecruiterId());
        if (!recruiter.getEmail().equals(recruiterEmail)) {
            throw new RuntimeException("recruteur non autorisé");
        }
        applicationRepository.delete(application);
    }

    public ApplicationResponse getApplicationById(int appId) {
        Application application = applicationRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("candidature non trouvée"));
        return ApplicationResponse.builder()
                .id(application.getId())
                .firstName(application.getCandidateFirstName())
                .lastName(application.getCandidateLastName())
                .phone(application.getCandidatePhone())
                .email(application.getCandidateEmail())
                .llmScore(application.getLlmScore())
                .llmAnalysis(application.getLlmAnalysis())
                .cvFilePath(application.getCvFilePath())
                .status(application.getStatus())
                .createdAt(application.getCreatedAt())
                .build();
    }

    public long ApplicationNumber() {
        return applicationRepository.count();
    }

    public long getApplicantCount(int jobId) {
        return applicationRepository.countByJobId(jobId);
    }


    public void deleteApplicationByAdmin(int id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        applicationRepository.delete(application);
    }

    public Page<ApplicationResponse> getApplicationsByStatus(int jobId, Application.ApplicationStatus status, Pageable pageable) {
        Page<Application> applications = applicationRepository.findAllByJobIdAndStatus(jobId, status, pageable);

        return applications.map(application -> ApplicationResponse.builder()
                .id(application.getId())
                .firstName(application.getCandidateFirstName())
                .lastName(application.getCandidateLastName())
                .phone(application.getCandidatePhone())
                .email(application.getCandidateEmail())
                .llmScore(application.getLlmScore())
                .llmAnalysis(application.getLlmAnalysis())
                .cvFilePath(application.getCvFilePath())
                .status(application.getStatus())
                .createdAt(application.getCreatedAt())
                .build());


    }

    public void updateStatus(int appId, Application.ApplicationStatus status) {
        Application application = applicationRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        if (status == Application.ApplicationStatus.ACCEPTED) {
            application.setStatus(status);
        }
        if (status == Application.ApplicationStatus.REFUSED) {
            application.setStatus(status);
        }
        if (status == Application.ApplicationStatus.REVIEWED) {
            application.setStatus(status);
        }
        applicationRepository.save(application);

    }

    public AdminApplicationStatsResponse getAdminStats() {
        LocalDateTime firstOfMonth = firstOfMonth();

        long total = applicationRepository.count();
        long pending = applicationRepository.countByStatus(Application.ApplicationStatus.PENDING);
        long analyzing = applicationRepository.countByStatus(Application.ApplicationStatus.ANALYZING);
        long analyzed = applicationRepository.countByStatus(Application.ApplicationStatus.ANALYZED)
                + analyzing; // include in-progress
        long accepted = applicationRepository.countByStatus(Application.ApplicationStatus.ACCEPTED);
        long refused = applicationRepository.countByStatus(Application.ApplicationStatus.REFUSED);

        long acceptedThisMonth = applicationRepository
                .countByStatusAndCreatedAtAfter(Application.ApplicationStatus.ACCEPTED, firstOfMonth);
        long refusedThisMonth = applicationRepository
                .countByStatusAndCreatedAtAfter(Application.ApplicationStatus.REFUSED, firstOfMonth);

        Double avgScore = applicationRepository.findAverageScore();
        Map<String, Long> monthlyData = new LinkedHashMap<>();
        List.of(
                        "January",
                        "February",
                        "March",
                        "April",
                        "May",
                        "June",
                        "July",
                        "August",
                        "September",
                        "October",
                        "November",
                        "December"
                )
                .forEach(month -> monthlyData.put(month, 0L));
        try {
            List<Object[]> rawMonthly = applicationRepository.countGroupedByMonth();
            if (rawMonthly != null) {
                rawMonthly.forEach(row -> {
                    String month = ((String) row[0]).trim();
                    Long count = ((Number) row[1]).longValue();
                    monthlyData.put(month, count);
                });
            }
        } catch (Exception e) {
            // log but don't crash — chart will just show all zeros
            log.warn("Failed to fetch monthly chart data: {}", e.getMessage());
        }

        return AdminApplicationStatsResponse.builder()
                .totalApplications(total)
                .pendingApplications(pending)
                .analyzedApplications(analyzed)
                .acceptedApplications(accepted)
                .refusedApplications(refused)
                .acceptedThisMonth(acceptedThisMonth)
                .refusedThisMonth(refusedThisMonth)
                .averageScore(avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 0.0)
                .monthlyData(monthlyData)
                .build();
    }

    // ── Recruiter ─────────────────────────────────────────────────────────────

    public RecruiterApplicationStatsResponse getRecruiterStats(String recruiterEmail) {
        // get recruiter's job IDs from job-service
        List<Integer> jobIds = jobServiceRestClient.getJobIdsByRecruiterEmail(recruiterEmail);

        if (jobIds == null || jobIds.isEmpty()) {
            return emptyRecruiterStats();
        }

        LocalDateTime firstOfMonth = firstOfMonth();
        LocalDateTime startOfWeek = startOfWeek();
        LocalDateTime startOfLastWeek = startOfWeek.minusWeeks(1);

        long total = applicationRepository.countByJobIdIn(jobIds);

        long pendingReview = applicationRepository
                .countByJobIdInAndStatus(jobIds, Application.ApplicationStatus.PENDING)
                + applicationRepository
                .countByJobIdInAndStatus(jobIds, Application.ApplicationStatus.ANALYZED)
                + applicationRepository
                .countByJobIdInAndStatus(jobIds, Application.ApplicationStatus.REVIEWED);

        long acceptedThisMonth = applicationRepository
                .countByJobIdInAndStatusAndCreatedAtAfter(
                        jobIds, Application.ApplicationStatus.ACCEPTED, firstOfMonth);

        long refusedThisMonth = applicationRepository
                .countByJobIdInAndStatusAndCreatedAtAfter(
                        jobIds, Application.ApplicationStatus.REFUSED, firstOfMonth);

        long thisWeek = applicationRepository
                .countByJobIdInAndCreatedAtBetween(jobIds, startOfWeek, LocalDateTime.now());

        long lastWeek = applicationRepository
                .countByJobIdInAndCreatedAtBetween(jobIds, startOfLastWeek, startOfWeek);

        Double avgScore = applicationRepository.findAverageScoreByJobIds(jobIds);


        // --- weeklyData ---
        Map<String, Long> weeklyData = new LinkedHashMap<>();
        List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
                .forEach(day -> weeklyData.put(day, 0L));

// Then try to fill from DB
        try {
            List<Object[]> rawWeekly = applicationRepository.countGroupedByDayOfWeek(jobIds, startOfWeek);
            if (rawWeekly != null) {
                rawWeekly.forEach(row -> {
                    String day = ((String) row[0]).trim();
                    Long count = ((Number) row[1]).longValue();
                    weeklyData.put(day, count);
                });
            }
        } catch (Exception e) {
            // log but don't crash — chart will just show all zeros
            log.warn("Failed to fetch weekly chart data: {}", e.getMessage());
        }
        // ------------------


        return RecruiterApplicationStatsResponse.builder()
                .totalApplications(total)
                .pendingReview(pendingReview)
                .acceptedThisMonth(acceptedThisMonth)
                .refusedThisMonth(refusedThisMonth)
                .applicationsThisWeek(thisWeek)
                .applicationsPreviousWeek(lastWeek)
                .averageScore(avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 0.0)
                .weeklyData(weeklyData)
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private LocalDateTime firstOfMonth() {
        return LocalDateTime.now()
                .withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime startOfWeek() {
        return LocalDateTime.now()
                .with(java.time.DayOfWeek.MONDAY)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    private RecruiterApplicationStatsResponse emptyRecruiterStats() {
        return RecruiterApplicationStatsResponse.builder()
                .totalApplications(0)
                .pendingReview(0)
                .acceptedThisMonth(0)
                .refusedThisMonth(0)
                .applicationsThisWeek(0)
                .applicationsPreviousWeek(0)
                .averageScore(0.0)
                .build();
    }


    public Boolean existByEmail(String email) {
        return applicationRepository.existsByCandidateEmail(email);
    }

    public Application createApp(Application application) {
        return applicationRepository.save(application);
    }

    public Optional<Application> findByEmail(String email) {
        return applicationRepository.findByCandidateEmail(email);
    }

    public List<CandidateApplicationResponse> getApplicationsByCandidate(int candidateId) {

        List<Application> applications =
                applicationRepository.findAllByCandidateId(candidateId);

        List<Integer> jobIds = applications.stream()
                .map(Application::getJobId)
                .toList();

        List<Job> jobs = jobServiceRestClient.findAllByJobById(jobIds);

        Map<Integer, Job> jobMap = jobs.stream()
                .collect(Collectors.toMap(Job::getId, job -> job));

        return applications.stream()
                .map(application -> {

                    Job job = jobMap.get(application.getJobId());

                    return CandidateApplicationResponse.builder()
                            .applicationId(application.getId())
                            .status(application.getStatus())
                            .appliedAt(application.getCreatedAt())

                            .jobId(job.getId())
                            .jobTitle(job.getTitle())
                            .company(job.getCompany())

                            .location(job.getLocation())

                            .build();
                })
                .toList();
    }

    public CandidateApplicationStatsResponse getCandidateStats(int id) {

        long total = applicationRepository.countByCandidateId(id);

        long accepted = applicationRepository
                .countByCandidateIdAndStatus(
                        id,
                        Application.ApplicationStatus.ACCEPTED
                );

        long refused = applicationRepository
                .countByCandidateIdAndStatus(
                        id,
                        Application.ApplicationStatus.REFUSED
                );

        long analyzed = applicationRepository
                .countByCandidateIdAndStatus(
                        id,
                        Application.ApplicationStatus.ANALYZED
                );
        long pending = applicationRepository
                .countByCandidateIdAndStatus(
                        id,
                        Application.ApplicationStatus.PENDING
                );

        return CandidateApplicationStatsResponse.builder()
                .total(total)
                .accepted(accepted)
                .refused(refused)
                .analyzed(analyzed)
                .pending(pending)
                .build();
    }

    public Page<AdminApplicationResponse> getAllApplications(Pageable pageable) {
        Page<Application> applications = applicationRepository.findAll(pageable);
        return mapToAdminResponses(applications);
    }
    public Page<ApplicationResponse> searchRecruiterApplications(
            String query,
            int jobId,
            Pageable pageable) {
        String normalizedQuery = query == null ? "" : query.trim();

        Specification<Application> specification = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("jobId"), jobId));
            if (!normalizedQuery.isBlank()) {
                String pattern = "%" + normalizedQuery.toLowerCase() + "%";
                Predicate candidateMatch = cb.or(
                        cb.like(cb.lower(root.get("candidateFirstName")), pattern),
                        cb.like(cb.lower(root.get("candidateLastName")), pattern),
                        cb.like(cb.lower(root.get("candidateEmail")), pattern)
                );
                predicates.add(candidateMatch);
            }
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Application> applications = applicationRepository.findAll(specification, pageable);
        return applications
                .map(application -> ApplicationResponse.builder()
                        .id(application.getId())
                        .firstName(application.getCandidateFirstName())
                        .lastName(application.getCandidateLastName())
                        .phone(application.getCandidatePhone())
                        .email(application.getCandidateEmail())
                        .llmScore(application.getLlmScore())
                        .llmAnalysis(application.getLlmAnalysis())
                        .cvFilePath(application.getCvFilePath())
                        .status(application.getStatus())
                        .createdAt(application.getCreatedAt())
                        .build());
    }

    public Page<AdminApplicationResponse> searchAdminApplications(
            String query,
            Application.ApplicationStatus status,
            Pageable pageable) {
        String normalizedQuery = query == null ? "" : query.trim();
        List<Integer> jobIds;
        if (!normalizedQuery.isBlank()) {
            jobIds = jobServiceRestClient.findJobIdsByTitle(normalizedQuery);
        } else {
            jobIds = List.of();
        }

        Specification<Application> specification = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (!normalizedQuery.isBlank()) {
                String pattern = "%" + normalizedQuery.toLowerCase() + "%";
                Predicate candidateMatch = cb.or(
                        cb.like(cb.lower(root.get("candidateFirstName")), pattern),
                        cb.like(cb.lower(root.get("candidateLastName")), pattern),
                        cb.like(cb.lower(root.get("candidateEmail")), pattern)
                );
                if (!jobIds.isEmpty()) {
                    Predicate jobMatch = root.get("jobId").in(jobIds);
                    predicates.add(cb.or(candidateMatch, jobMatch));
                } else {
                    predicates.add(candidateMatch);
                }
            }
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Application> applications = applicationRepository.findAll(specification, pageable);
        return mapToAdminResponses(applications);
    }

    private Page<AdminApplicationResponse> mapToAdminResponses(Page<Application> applications) {
        List<Integer> ids = applications.stream()
                .map(Application::getJobId)
                .distinct()
                .toList();

        List<Job> jobs = jobServiceRestClient.findAllByJobById(ids);
        Map<Integer, Job> jobMap = jobs.stream()
                .collect(Collectors.toMap(Job::getId, job -> job));


        List<Integer> recruiterIds=jobs.stream().map(Job::getRecruiterId).distinct().toList();
        List<Recruiter> recruiters=recruiterClient.findRecruitersByIds(recruiterIds);

        Map<Integer,Recruiter> recruiterMap=recruiters.stream()
                .collect(Collectors.toMap(Recruiter::getId,recruiter -> recruiter));



        return applications.map(application -> {

            Job job = jobMap.get(application.getJobId());
            if (job == null) {
                throw new RuntimeException("Job not found with id: " + application.getJobId());
            }
            Recruiter recruiter=recruiterMap.get(job.getRecruiterId());
            if (recruiter == null) {
                throw new RuntimeException("Recruiter not found with id: " + job.getRecruiterId());
            }

            return AdminApplicationResponse.builder()
                    .id(application.getId())
                    .firstName(application.getCandidateFirstName())
                    .lastName(application.getCandidateLastName())
                    .phone(application.getCandidatePhone())
                    .email(application.getCandidateEmail())
                    .llmScore(application.getLlmScore())
                    .llmAnalysis(application.getLlmAnalysis())
                    .cvFilePath(application.getCvFilePath())
                    .status(application.getStatus())
                    .createdAt(application.getCreatedAt())
                    //Job-details
                    .title(job.getTitle())
                    .jobCreatedAt(job.getCreatedAt())
                    .description(job.getDescription())
                    .location(job.getLocation())
                    //Recruiter-details
                    .recruiterFirstName(recruiter.getFirstName())
                    .recruiterLastName(recruiter.getLastName())
                    .recruiterEmail(recruiter.getEmail())
                    .build();
        });
    }
}
