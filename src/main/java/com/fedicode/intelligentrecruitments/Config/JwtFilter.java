package com.fedicode.intelligentrecruitments.Config;

import com.fedicode.intelligentrecruitments.Service.JwtService;
import com.fedicode.intelligentrecruitments.Service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.naming.Context;
import java.io.IOException;
@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private JwtService jwtService;

    private MyUserDetailsService myUserDetailsService;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        String path=request.getServletPath();
        return path.startsWith("/auth");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader=request.getHeader("Authorization");
        String token= "";
        String email= "";

        if(authHeader!=null && authHeader.startsWith("Barrer ")) {
            token = authHeader.substring(7);
            email = jwtService.extractEmail(token);
        }

        if(email!=null && !email.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
              UserDetails userDetails = myUserDetailsService.loadUserByUsername(email);

            if (jwtService.isTokenValid(token, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request,response);



    }
}
