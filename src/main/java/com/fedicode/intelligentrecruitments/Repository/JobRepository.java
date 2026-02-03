package com.fedicode.intelligentrecruitments.Repository;

import com.fedicode.intelligentrecruitments.Entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job,Integer> {

    List<Job> findByRecruiterId(int id);

    List<Job> findByRecruiterIdOrderByCreatedAtDesc(int id);




}
