package com.fedicode.intelligentrecruitments.Service;

import com.fedicode.intelligentrecruitments.Dto.LoginRequest;
import com.fedicode.intelligentrecruitments.Dto.AuthResponse;
import com.fedicode.intelligentrecruitments.Entity.Recruiter;
import com.fedicode.intelligentrecruitments.Repository.RecruiterRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RecruiterService {

    private RecruiterRepository recruiterRepository;
    private PasswordEncoder passwordEncoder;

//    public Recruiter register(AuthResponse request, String email){
//         if(recruiterRepository.findByEmail(request.getEmail()).isPresent()){
//             throw new RuntimeException("Email déja utilisé");
//        }
//         Recruiter recruiter1=Recruiter.builder()
//                 .email(request.getEmail())
//                 .password(passwordEncoder.encode(request.getPassword()))
//                 .companyName(request.getCompanyName())
//                 .firstName(request.getFirstName())
//                 .lastName(request.getLastName())
//                 .build();
//         return recruiterRepository.save(recruiter1);
//    }
//
//    public Recruiter login(LoginRequest request){
//
//        Recruiter recruiter=recruiterRepository.findByEmail(request.getEmail())
//                .orElseThrow(()->new RuntimeException("Email ou mot de passe  incorrect"));
//
//        if(!passwordEncoder.matches(request.getPassword(),recruiter.getPassword())){
//            throw new RuntimeException("Email ou mot de passe incorrect");
//        }
//        return recruiter;


//    public void changePassword(RecruiterLoginRequest)






}
