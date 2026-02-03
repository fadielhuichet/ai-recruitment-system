package com.fedicode.intelligentrecruitments.Service;

import com.fedicode.intelligentrecruitments.Dto.AuthResponse;
import com.fedicode.intelligentrecruitments.Dto.LoginRequest;
import com.fedicode.intelligentrecruitments.Dto.RegisterRequest;
import com.fedicode.intelligentrecruitments.Entity.Recruiter;
import com.fedicode.intelligentrecruitments.Repository.RecruiterRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    private AuthenticationManager authenticationManager;
    private RecruiterRepository recruiterRepository;
    private JwtService jwtService;
    private PasswordEncoder passwordEncoder;


    public AuthResponse register(RegisterRequest request){
        if(recruiterRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email deja exist");

        Recruiter recruiter1=Recruiter.builder()
                .email(request.getEmail())
                .companyName(request.getCompanyName())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .build();
        recruiterRepository.save(recruiter1);

        String Token=jwtService.generateToken(recruiter1.getId(),recruiter1.getEmail());

        log.info("Nouveau recruteur inscrit:"+recruiter1.getEmail());


        return AuthResponse.builder()
                .token(Token)
                .email(request.getEmail())
                .companyName(request.getCompanyName())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .message("Inscription réussie")
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword())
            );
        }
        catch (BadCredentialsException e){
            throw new RuntimeException("Email ou mot de passe incorrect");
        }
        Recruiter recruiter= recruiterRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()->new UsernameNotFoundException("Recruteur non trouvé"));

        String token= jwtService.generateToken(recruiter.getId(),recruiter.getEmail());

//        if(!passwordEncoder.matches(loginRequest.getPassword(),recruiter.getPassword()))
//            throw new RuntimeException("Mot de passe incorrect");

        return AuthResponse.builder()
                .token(token)
                .email(recruiter.getEmail())
                .firstName(recruiter.getFirstName())
                .lastName(recruiter.getLastName())
                .companyName(recruiter.getCompanyName())
                .message("Connexion réussie")
                .build();

    }
}
