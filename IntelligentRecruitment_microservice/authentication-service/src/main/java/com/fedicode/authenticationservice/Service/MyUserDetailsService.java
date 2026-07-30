package com.fedicode.authenticationservice.Service;

import com.fedicode.authenticationservice.Feign.AdminServiceRestClient;
import com.fedicode.authenticationservice.Feign.CandidateClient;
import com.fedicode.authenticationservice.Feign.RecruiterServiceRestClient;
import com.fedicode.authenticationservice.model.Admin;
import com.fedicode.authenticationservice.model.Candidate;
import com.fedicode.authenticationservice.model.Recruiter;

import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final CandidateClient candidateClient;
    private RecruiterServiceRestClient recruiterClient;
    private AdminServiceRestClient adminClient;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Recruiter> recruiterOpt = recruiterClient.findByEmail(email);
        if (recruiterOpt.isPresent()) {
            Recruiter recruiter = recruiterOpt.get();
            return new User(
                    recruiter.getEmail(),
                    recruiter.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_RECRUITER"))
            );
        }
        Optional<Admin> adminOpt = adminClient.findByEmail(email);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
             return new User(
                    admin.getEmail(),
                    admin.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }
        Optional<Candidate> candidateOpt= candidateClient.findByEmail(email);
        if (candidateOpt.isPresent()){
            Candidate candidate=candidateOpt.get();
            return new User(
                    candidate.getEmail(),
                    candidate.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_CANDIDATE"))
            );
        }
        throw new UsernameNotFoundException("Utilisateur non trouvé," + email);
    }
}
