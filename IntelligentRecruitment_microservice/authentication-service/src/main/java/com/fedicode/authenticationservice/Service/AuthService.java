package com.fedicode.authenticationservice.Service;

import com.fedicode.authenticationservice.CustomException.*;
import com.fedicode.authenticationservice.Dto.*;
import com.fedicode.authenticationservice.Feign.AdminServiceRestClient;
import com.fedicode.authenticationservice.Feign.CandidateClient;
import com.fedicode.authenticationservice.Feign.RecruiterServiceRestClient;
import com.fedicode.authenticationservice.model.*;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    private AuthenticationManager authenticationManager;
    private RecruiterServiceRestClient recruiterClient;
    private CandidateClient candidateClient;
    private JwtService jwtService;
    private PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private AdminServiceRestClient adminClient;
    private final FileService fileService;


    public AuthResponse candidateRegister(CandidateRegisterRequest request) {
        if (candidateClient.existsByEmail(request.getEmail()))
            throw new EmailAlreadyExist("Email is already exist");

        String cvFilePath=fileService.saveFile(request.getCv());

        Candidate candidate1 = Candidate.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .country(request.getCountry())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .cvFilePath(cvFilePath)
                .role(Role.CANDIDATE)
                .status(Status.ACTIVE)
                .build();
        candidateClient.saveCandidate(candidate1);

        String Token = jwtService.generateToken(candidate1.getId(), candidate1.getEmail(),candidate1.getRole().name(),candidate1.getStatus().name());

        log.info("Nouveau candidat inscrit:" + candidate1.getEmail());


        return AuthResponse.builder()
                .token(Token)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .message("Inscription réussie")
                .build();
    }
    private static final Set<String> FREE_EMAIL_DOMAINS = Set.of(
            "gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "live.com",
            "icloud.com", "aol.com", "protonmail.com", "mail.com", "zoho.com",
            "yandex.com", "gmx.com", "tutanota.com", "msn.com", "me.com",
            "inbox.com", "fastmail.com", "hushmail.com", "rediffmail.com",
            "yahoo.fr", "hotmail.fr", "laposte.net", "wanadoo.fr", "orange.fr",
            "free.fr", "sfr.fr", "bbox.fr","mailinator.com", "tempmail.com", "guerrillamail.com", "10minutemail.com",
            "throwaway.email", "trashmail.com", "yopmail.com"
    );

    public static boolean isWorkEmail(String email) {
        if (email == null || !email.contains("@")) return false;
        String[] parts = email.split("@");
        if (parts.length != 2 || parts[1].isBlank()) return false;
        String domain = parts[1].toLowerCase().trim();
        return !FREE_EMAIL_DOMAINS.contains(domain);
    }
    public AuthResponse register(RegisterRequest request) {
        if (recruiterClient.existsByEmail(request.getEmail()))
            throw new EmailAlreadyExist("Email deja exist");

        if (!isWorkEmail(request.getEmail())){
            throw new IsNotWorkEmail("Registration requires a work email address. Please use your company email.");
        }


        Recruiter recruiter1 = Recruiter.builder()
                .email(request.getEmail())
                .companyName(request.getCompanyName())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.RECRUITER)
                .status(Status.ACTIVE)
                .build();
        recruiterClient.create(recruiter1);

        String Token = jwtService.generateToken(recruiter1.getId(), recruiter1.getEmail(),recruiter1.getRole().name(),recruiter1.getStatus().name());

        log.info("Nouveau recruteur inscrit:" + recruiter1.getEmail());


        return AuthResponse.builder()
                .token(Token)
                .email(request.getEmail())
                .companyName(request.getCompanyName())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .message("Inscription réussie")
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }
        Optional<Candidate> candidateOpt= candidateClient.findByEmail(loginRequest.getEmail());
        Optional<Recruiter> recruiterOpt = recruiterClient.findByEmail(loginRequest.getEmail());
        if (recruiterOpt.isPresent()) {
            Recruiter recruiter = recruiterOpt.get();
            if (recruiter.getStatus()==Status.SUSPENDED){
                throw new AccountSuspended("Your account is temporarily suspended. Please contact support");
            }
            String token = jwtService.generateToken(recruiter.getId(), recruiter.getEmail(),recruiter.getRole().name(),recruiter.getStatus().name());

//        if(!passwordEncoder.matches(loginRequest.getPassword(),recruiter.getPassword()))
//            throw new RuntimeException("Mot de passe incorrect");

            return AuthResponse.builder()
                    .token(token)
                    .email(recruiter.getEmail())
                    .firstName(recruiter.getFirstName())
                    .lastName(recruiter.getLastName())
                    .companyName(recruiter.getCompanyName())
                    .role(recruiter.getRole())
                    .message("Connexion réussie")
                    .build();

        }
        else if (candidateOpt.isPresent()) {
            Candidate candidate=candidateOpt.get();
            if (candidate.getStatus()==Status.SUSPENDED){
                throw new AccountSuspended("Your account is temporarily suspended. Please contact support");
            }

            String token=jwtService.generateToken(candidate.getId(),candidate.getEmail(),candidate.getRole().name(),candidate.getStatus().name());

            return AuthResponse.builder()
                    .token(token)
                    .email(candidate.getEmail())
                    .firstName(candidate.getFirstName())
                    .lastName(candidate.getLastName())
                    .role(candidate.getRole())
                    .message("Connexion réussie")
                    .build();

        }
        Admin admin=adminClient.findByEmail(loginRequest.getEmail())
                .orElseThrow(()->new EmailNotExistException("email does not exist"));

        String token=jwtService.generateToken(admin.getId(),admin.getEmail(), admin.getRole().name(),null);
        return AuthResponse.builder()
                .token(token)
                .email(admin.getEmail())
                .username(admin.getUsername())
                .role(admin.getRole())
                .message("connexion réussie")
                .build();
    }

    public void emailVerification(String email){
        if(!recruiterClient.existsByEmail(email)&&!candidateClient.existsByEmail(email)) {
            throw new EmailNotExistException("Email does not exist");
        }
        emailService.sendVerificationCode(email);
    }

    public Object verifyCode(ChangePasswordRequest request) {

        // Check recruiter first
        Optional<Recruiter> recruiterOptional =
                recruiterClient.findByEmail(request.getEmail());

        if (recruiterOptional.isPresent()) {

            Recruiter recruiter = recruiterOptional.get();

            if (request.getCodeEmail() == null ||
                    !request.getCodeEmail().equals(recruiter.getVerificationCode())) {
                throw new InvalidCodeException("Invalid code");
            }

            if (recruiter.getCodeExpiration().isBefore(LocalDateTime.now())) {
                throw new CodeExpiredException("Code expired");
            }

            return recruiter;
        }

        // Check candidate/application
        Optional<Candidate> candidateOptional =
                candidateClient.findByEmail(request.getEmail());

        if (candidateOptional.isPresent()) {

            Candidate candidate = candidateOptional.get();

            if (request.getCodeEmail() == null ||
                    !request.getCodeEmail().equals(candidate.getVerificationCode())) {
                throw new InvalidCodeException("Invalid code");
            }

            if (candidate.getCodeExpiration().isBefore(LocalDateTime.now())) {
                throw new CodeExpiredException("Code expired");
            }

            return candidate;
        }

        throw new EmailNotExistException("Email does not exist");
    }

    public void changePassword(ChangePasswordRequest request) {

        Object user = verifyCode(request);

        if (user instanceof Recruiter recruiter) {

            recruiter.setVerificationCode(null);
            recruiter.setCodeExpiration(null);
            recruiter.setPassword(
                    passwordEncoder.encode(request.getNewPassword())
            );

            recruiterClient.create(recruiter);

        } else if (user instanceof Candidate candidate) {

            candidate.setVerificationCode(null);
            candidate.setCodeExpiration(null);
            candidate.setPassword(
                    passwordEncoder.encode(request.getNewPassword())
            );

            candidateClient.saveCandidate(candidate);

        } else {
            throw new RuntimeException("Unsupported user type");
        }
    }


    public CandidateResponse getCandidateInfo(String email) {
        Candidate candidate=candidateClient.findByEmail(email)
                .orElseThrow(()->new RuntimeException("Recruiter does not exist"));
        return CandidateResponse.builder()
                .id(candidate.getId())
                .firstName(candidate.getFirstName())
                .lastName(candidate.getLastName())
                .email(candidate.getEmail())
                .dateOfBirth(candidate.getDateOfBirth())
                .phone(candidate.getPhone())
                .profileImage(candidate.getProfileImage())
                .country(candidate.getCountry())
                .cvFilePath(candidate.getCvFilePath())
                .build();
    }
    public AuthResponse getRecruiterInfo(String email) {
        Recruiter recruiter=recruiterClient.findByEmail(email)
                .orElseThrow(()->new RuntimeException("Recruiter does not exist"));
        return AuthResponse.builder()
                .firstName(recruiter.getFirstName())
                .lastName(recruiter.getLastName())
                .email(recruiter.getEmail())
                .companyName(recruiter.getCompanyName())
                .phone(recruiter.getPhone())
                .profileImage(recruiter.getProfileImage())
                .build();
    }

    public AdminResponse getAdminInfo(String email) {
            Admin admin=adminClient.findByEmail(email)
                    .orElseThrow(()->new RuntimeException("Admin does not exist"));
            return AdminResponse.builder()
                    .username(admin.getUsername())
                    .email(admin.getEmail())
                    .password(admin.getPassword())
                    .phone(admin.getPhone())
                    .createdAt(admin.getCreatedAt())
                    .build();
        }


    public void sendContactMessage(ContactRequest request) {
        if (request == null || request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        emailService.sendContactMail(request);
    }
}
