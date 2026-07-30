package com.fedicode.applicationservice.Repository;

import com.fedicode.applicationservice.Entity.Application;
import com.fedicode.applicationservice.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application,Integer>, JpaSpecificationExecutor<Application> {

    Optional<Application> findByCandidateEmail(String candidateEmail);

    List<Application> findByJobId(int id);

    List<Application> findByJobIdOrderByLlmScoreDesc(int id);

    List<Application> findByJobIdOrderByCreatedAtDesc(int id);


    long countByJobId(int id);

    Page<Application> findAllByJobIdAndStatus(int id,Application.ApplicationStatus status,Pageable pageable);

    // ── admin stats ───────────────────────────────────────────
    long countByStatus(Application.ApplicationStatus status);

    long countByStatusAndCreatedAtAfter(Application.ApplicationStatus status, LocalDateTime date);

    @Query("SELECT AVG(a.llmScore) FROM Application a WHERE a.llmScore IS NOT NULL")
    Double findAverageScore();

    // ── recruiter stats ───────────────────────────────────────
    long countByJobIdIn(List<Integer> jobIds);

    long countByJobIdInAndStatus(List<Integer> jobIds, Application.ApplicationStatus status);

    long countByJobIdInAndStatusAndCreatedAtAfter(
            List<Integer> jobIds,
            Application.ApplicationStatus status,
            LocalDateTime date);

    long countByJobIdInAndCreatedAtBetween(
            List<Integer> jobIds,
            LocalDateTime from,
            LocalDateTime to);

    @Query("SELECT AVG(a.llmScore) FROM Application a " +
            "WHERE a.jobId IN :jobIds AND a.llmScore IS NOT NULL")
    Double findAverageScoreByJobIds(@Param("jobIds") List<Integer> jobIds);



    @Query(value = """
    SELECT TO_CHAR(a.created_at, 'Day') AS day_name, COUNT(a.id)
    FROM application a
    WHERE a.job_id IN :jobIds
    AND a.created_at >= :startOfWeek
    GROUP BY TO_CHAR(a.created_at, 'Day')
    """, nativeQuery = true)
    List<Object[]> countGroupedByDayOfWeek(
            @Param("jobIds") List<Integer> jobIds,
            @Param("startOfWeek") LocalDateTime startOfWeek
    );

    @Query(value = """
    SELECT TO_CHAR(a.created_at, 'FMMonth') AS month_name,
           COUNT(a.id)
    FROM application a
    GROUP BY EXTRACT(MONTH FROM a.created_at),
             TO_CHAR(a.created_at, 'FMMonth')
    ORDER BY EXTRACT(MONTH FROM a.created_at)
    """, nativeQuery = true)
    List<Object[]> countGroupedByMonth();

    Boolean existsByCandidateEmail(String email);

    boolean existsByJobIdAndCandidateEmail(
            int jobId,
            String candidateEmail
    );

    List<Application> findAllByCandidateId(int id);

    long countByCandidateIdAndStatus(int id, Application.ApplicationStatus applicationStatus);

    long countByCandidateId(int id);

    Page<Application> findAllByJobId(int JobId,Specification<Application> specification, Pageable pageable);
}
