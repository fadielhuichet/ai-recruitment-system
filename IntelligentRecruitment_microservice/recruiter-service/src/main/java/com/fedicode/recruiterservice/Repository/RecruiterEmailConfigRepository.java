package com.fedicode.recruiterservice.Repository;

import com.fedicode.recruiterservice.Entity.RecruiterEmailConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruiterEmailConfigRepository  extends JpaRepository<RecruiterEmailConfig, String> {
}
