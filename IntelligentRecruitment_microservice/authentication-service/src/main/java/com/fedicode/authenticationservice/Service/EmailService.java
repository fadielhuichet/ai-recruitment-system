package com.fedicode.authenticationservice.Service;

import com.fedicode.authenticationservice.CustomException.EmailNotExistException;
import com.fedicode.authenticationservice.Dto.ContactRequest;
import com.fedicode.authenticationservice.Feign.CandidateClient;
import com.fedicode.authenticationservice.Feign.RecruiterServiceRestClient;
import com.fedicode.authenticationservice.model.Candidate;
import com.fedicode.authenticationservice.model.Recruiter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class EmailService {
    private final MailSender mailSender;

    private final RecruiterServiceRestClient recruiterClient;
    private final CandidateClient candidateClient;

    @Value("${app.contact.to}")
    private String contactTo;

    @Value("${spring.mail.username}")
    private String mailFrom;


    public void sendVerificationCode(String to) {

        String code = generateCode();
        String subject = "Email verification code";

        Optional<Recruiter> recruiterOpt = recruiterClient.findByEmail(to);

        if (recruiterOpt.isPresent()) {

            Recruiter recruiter = recruiterOpt.get();

            recruiter.setVerificationCode(code);
            recruiter.setCodeExpiration(LocalDateTime.now().plusMinutes(10));

            recruiterClient.create(recruiter);

        } else {

            Optional<Candidate> candidateOpt =
                    candidateClient.findByEmail(to);

            if (candidateOpt.isPresent()) {

                Candidate candidate = candidateOpt.get();

                candidate.setVerificationCode(code);
                candidate.setCodeExpiration(LocalDateTime.now().plusMinutes(10));

                candidateClient.saveCandidate(candidate);

            } else {
                throw new EmailNotExistException("Email does not exist");
            }
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(generateText(code));

        mailSender.send(message);
    }

    private String generateCode() {
        Random rnd= new Random();
        int number=rnd.nextInt(1000000);
        return String.format("%06d",number);
    }

    private String generateText(String code) {
        return "Hello,\n" +
                "\n" +
                "We received a request to verify your email address.\n" +
                "\n" +
                "Your verification code is:\n" +
                "\n" +
                 code+
                "\n" +
                "\n"+
                "Please enter this code in the application to complete the verification process.\n" +
                "This code is valid for the next 10 minutes.\n" +
                "\n" +
                "If you did not request this code, please ignore this email. No action is required.\n" +
                "\n" +
                "Best regards,\n" +
                "\n"+
                "The Support Team";
    }

    public void sendContactMail(ContactRequest contactRequest){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(contactTo);
        message.setFrom(mailFrom);
        message.setReplyTo(contactRequest.getEmail());
        message.setSubject("Contact form: " + contactRequest.getSubject());
        message.setText(
            "From: " + contactRequest.getFirstName() + " <" + contactRequest.getEmail() + ">\n\n" +
            contactRequest.getMessage()
        );

        mailSender.send(message);
    }

}
