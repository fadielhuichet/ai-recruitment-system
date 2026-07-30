package com.fedicode.applicationservice.Service;

import com.fedicode.applicationservice.Entity.Application;
import com.fedicode.applicationservice.Repository.ApplicationRepository;
import com.fedicode.applicationservice.Dto.EmailConfigDto;
import com.fedicode.applicationservice.feign.JobServiceRestClient;
import com.fedicode.applicationservice.feign.RecruiterServiceRestClient;

import com.fedicode.applicationservice.model.Job;
import com.fedicode.applicationservice.model.Recruiter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
@Slf4j
public class EmailService {

    private final MailSender mailSender;
    private final ApplicationRepository applicationRepository;
    private final RecruiterServiceRestClient recruiterServiceClient;
    private final JobServiceRestClient jobServiceClient;

    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendAcceptedEmail(int applicationId) {
        try {
            Application application = applicationRepository.findById(applicationId)
                    .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

            application.setStatus(Application.ApplicationStatus.ACCEPTED);
            applicationRepository.save(application);

            Job job = jobServiceClient.findJobById(application.getJobId());
            Recruiter recruiter = recruiterServiceClient.findRecruiterById(job.getRecruiterId());
            String recruiterEmail = recruiter.getEmail();
            EmailConfigDto config = fetchConfig(recruiterEmail);

            String subject = config.getAcceptSubject();
            String body    = personalize(config.getAcceptBody(), application);

            send(application.getCandidateEmail(), subject, body);
            log.info("Acceptance email sent to {}", application.getCandidateEmail());

        } catch (Exception e) {
            log.error("Failed to send acceptance email for applicationId {}: {}",
                    applicationId, e.getMessage(), e);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendRefusedEmail(int applicationId) {
        try {
            Application application = applicationRepository.findById(applicationId)
                    .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

            application.setStatus(Application.ApplicationStatus.REFUSED);
            applicationRepository.save(application);


            Job job = jobServiceClient.findJobById(application.getJobId());
            Recruiter recruiter = recruiterServiceClient.findRecruiterById(job.getRecruiterId());
            String recruiterEmail = recruiter.getEmail();
            EmailConfigDto config = fetchConfig(recruiterEmail);

            String subject = config.getRefuseSubject();
            String body    = personalize(config.getRefuseBody(), application);

            send(application.getCandidateEmail(), subject, body);
            log.info("Refusal email sent to {}", application.getCandidateEmail());

        } catch (Exception e) {
            log.error("Failed to send refusal email for applicationId {}: {}",
                    applicationId, e.getMessage(), e);
        }
        return CompletableFuture.completedFuture(null);
    }


    private EmailConfigDto fetchConfig(String recruiterEmail) {
        try {
            return recruiterServiceClient.getRecruiterEmailConfig(recruiterEmail);
        } catch (Exception e) {
            log.warn("Could not fetch email config for recruiter '{}', falling back to defaults. Reason: {}",
                    recruiterEmail, e.getMessage());
            return defaultFallback();
        }
    }

    private String personalize(String template, Application app) {
        return template
                .replace("{{firstName}}", app.getCandidateFirstName())
                .replace("{{lastName}}",  app.getCandidateLastName())
                .replace("{{fullName}}",  app.getCandidateFirstName() + " " + app.getCandidateLastName());
    }

    private void send(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }


    private EmailConfigDto defaultFallback() {
        return EmailConfigDto.builder()
                .acceptSubject("Congratulations! Your application has been accepted")
                .acceptBody("""
                        Dear {{fullName}},

                        We are pleased to inform you that your application has been accepted!
                        Our team will reach out to you shortly with the next steps.

                        Best Regards,
                        The Recruitment Team""")
                .refuseSubject("Your Internship Application")
                .refuseBody("""
                        Dear {{fullName}},

                        Thank you for applying.
                        Unfortunately, after careful consideration, we have decided not to \
                        move forward with your application at this time.

                        We encourage you to apply again in the future and wish you the best.

                        Best Regards,
                        The Recruitment Team""")
                .build();
    }
}