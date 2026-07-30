package com.fedicode.jobservice.Repository;

import com.fedicode.jobservice.Entity.Job;
import com.fedicode.jobservice.Entity.JobCategory;
import com.fedicode.jobservice.Entity.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface JobRepository extends JpaRepository<Job,Integer>, JpaSpecificationExecutor<Job> {

    List<Job> findByRecruiterId(int id);

    List<Job> findByRecruiterIdOrderByCreatedAtDesc(int id);

    Page<Job> getAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByStatus(JobStatus jobStatus);
    default long countActiveJobs() {
        return countByStatus(JobStatus.ACTIVE);
    }

    Page<Job> findByCategoryOrderByCreatedAtDesc(JobCategory category, Pageable pageable);

    long countByCreatedAtAfter(LocalDateTime localDateTime);


    //recruiter-Status
    long countByRecruiterIdAndStatus(int recruiterId, JobStatus status);
    long countByRecruiterIdAndCreatedAtAfter(int recruiterId, LocalDateTime date);
    long countByRecruiterId(int recruiterId);


    List<Job> findTop3ByOrderByCreatedAtDesc();

    List<Job> findAllByIdIn(List<Integer> ids);
}
