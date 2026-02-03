package com.fedicode.intelligentrecruitments.Service;

import com.fedicode.intelligentrecruitments.Entity.Recruiter;
import com.fedicode.intelligentrecruitments.Repository.RecruiterRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private RecruiterRepository recruiterRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Recruiter recruiter= recruiterRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("Recruteur non trouvé "+email));

        return new User(
                recruiter.getEmail(),
                recruiter.getPassword(),
                Collections.emptyList()
        );
    }
}
