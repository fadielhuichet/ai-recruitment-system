package com.fedicode.intelligentrecruitments.Service;

import com.fedicode.intelligentrecruitments.Entity.Admin;
import com.fedicode.intelligentrecruitments.Entity.Job;
import com.fedicode.intelligentrecruitments.Entity.Recruiter;
import com.fedicode.intelligentrecruitments.Repository.AdminRepository;
import com.fedicode.intelligentrecruitments.Repository.JobRepository;
import com.fedicode.intelligentrecruitments.Repository.RecruiterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AdminService {

    private AdminRepository adminRepository;
    private RecruiterRepository recruiterRepository;
    private JobRepository jobRepository;

    public List<Recruiter> getAllRecruiter(LocalDateTime createdAt) {
        return recruiterRepository.findAllOrderByCreatedAtDesc(createdAt);
    }
    public Optional<Recruiter> getRecruiterById(int id){
        return recruiterRepository.findById(id);
    }

    public List<Job> getJobByRecruiterId(int id){
        return jobRepository.findByRecruiterIdOrderByCreatedAtDesc(id);
    }
}
