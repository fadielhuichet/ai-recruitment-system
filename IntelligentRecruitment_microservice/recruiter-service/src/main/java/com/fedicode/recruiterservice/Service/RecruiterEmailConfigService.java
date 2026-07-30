package com.fedicode.recruiterservice.Service;

import com.fedicode.recruiterservice.Dto.EmailConfigDto;
import com.fedicode.recruiterservice.Entity.RecruiterEmailConfig;
import com.fedicode.recruiterservice.Repository.RecruiterEmailConfigRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RecruiterEmailConfigService {

    private final RecruiterEmailConfigRepository repo;

    public EmailConfigDto getConfig(String recruiterEmail) {
        return repo.findById(recruiterEmail)
                .map(c -> EmailConfigDto.builder()
                        .acceptSubject(c.getAcceptSubject())
                        .acceptBody(c.getAcceptBody())
                        .refuseSubject(c.getRefuseSubject())
                        .refuseBody(c.getRefuseBody())
                        .build())
                .orElse(defaultConfig());
    }

    public void saveConfig(String recruiterEmail, EmailConfigDto dto) {
        RecruiterEmailConfig config = repo.findById(recruiterEmail)
                .orElse(RecruiterEmailConfig.builder()
                        .recruiterEmail(recruiterEmail)
                        .build());

        if (dto.getAcceptSubject() != null) config.setAcceptSubject(dto.getAcceptSubject());
        if (dto.getAcceptBody()    != null) config.setAcceptBody(dto.getAcceptBody());
        if (dto.getRefuseSubject() != null) config.setRefuseSubject(dto.getRefuseSubject());
        if (dto.getRefuseBody()    != null) config.setRefuseBody(dto.getRefuseBody());

        repo.save(config);
    }

    public static EmailConfigDto defaultConfig() {
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