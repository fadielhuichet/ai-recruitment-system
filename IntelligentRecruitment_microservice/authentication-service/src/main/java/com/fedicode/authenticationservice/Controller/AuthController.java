package com.fedicode.authenticationservice.Controller;

import com.fedicode.authenticationservice.Dto.*;
import com.fedicode.authenticationservice.Service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private AuthService authService;


    @PostMapping("/candidateRegister")
    public ResponseEntity<AuthResponse> candidateRegister(@ModelAttribute CandidateRegisterRequest request){

        AuthResponse response=authService.candidateRegister(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request){

        AuthResponse response=authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        AuthResponse response=authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verifyEmail")
    public ResponseEntity<?> verifEmail(@RequestBody ChangePasswordRequest request){
        authService.emailVerification(request.getEmail());
        return ResponseEntity.ok(Map.of("message","code envoyé"));
    }
    @PostMapping("/verifyCode")
    public ResponseEntity<?> verifyCode(@RequestBody ChangePasswordRequest request){
        authService.verifyCode(request);
        return ResponseEntity.ok(Map.of("message","code valide","email",request.getEmail()));
    }
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request){
        authService.changePassword(request);
        return ResponseEntity.ok(Map.of("message","Mot de passe modifié avec succès"));
    }

    @PostMapping("/contact")
    public ResponseEntity<?> sendContact(@RequestBody ContactRequest request) {
        authService.sendContactMessage(request);
        return ResponseEntity.ok(Map.of("message", "message sent"));
    }

    @GetMapping("/recruiter-info")
    public AuthResponse recruiterInfo(@RequestHeader("X-User-Email")String email){
        return authService.getRecruiterInfo(email);
    }
    @GetMapping("/admin-info")
    public AdminResponse getAdminInfo(@RequestHeader("X-User-Email")String email){
        return authService.getAdminInfo(email);
    }

    //----------new-----
    @GetMapping("/candidate-info")
    public CandidateResponse getCandidateInfo(@RequestHeader("X-User-Email")String email){
        return authService.getCandidateInfo(email);
    }


}
