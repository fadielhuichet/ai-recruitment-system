package com.fedicode.intelligentrecruitments.Repository;

import com.fedicode.intelligentrecruitments.Entity.Application;
import com.fedicode.intelligentrecruitments.Entity.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecruiterRepository extends JpaRepository<Recruiter,Integer> {

    Optional<Recruiter> findByEmail(String email);

    Boolean existsByEmail(String email);


}
