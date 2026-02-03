package com.fedicode.intelligentrecruitments.Service;

import com.fedicode.intelligentrecruitments.Entity.Application;
import com.fedicode.intelligentrecruitments.Repository.ApplicationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ApplicationService {

    private ApplicationRepository applicationRepository;

    public Application createApplication(Application application){

        return applicationRepository.save(application);
    }

    public List<Application> getAllApplicationsByJob(int id){
        return applicationRepository.findByJobId(id);
    }

    public List<Application> getApplicationByScodeDesc(int id){
        return applicationRepository.findByJobIdOrderByLlmScoreDesc(id);
    }

    public List<Application> getApplicationByCreationDateDesc(int id){
        return applicationRepository.findByJobIdOrderByCreatedAtDesc(id);
    }


}
