package com.fedicode.intelligentrecruitments.Repository;

import com.fedicode.intelligentrecruitments.Entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application,Integer> {

    List<Application> findByJobId(int id);

    List<Application> findByJobIdOrderByLlmScoreDesc(int id);

    List<Application> findByJobIdOrderByCreatedAtDesc(int id);


    long countByJobId(int id);


}
