package com.fedicode.intelligentrecruitments.Service;

import com.fedicode.intelligentrecruitments.Entity.Job;
import com.fedicode.intelligentrecruitments.Repository.JobRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class JobService {

    private JobRepository jobRepository;


    public Job createJob(Job job){
        return jobRepository.save(job);
    }
    public List<Job> getJobRecruiter(int id){
        return jobRepository.findByRecruiterId(id);
    }

    public List<Job> getJobRecruiterByCreationDate(int id){
        return jobRepository.findByRecruiterIdOrderByCreatedAtDesc(id);
    }
}


