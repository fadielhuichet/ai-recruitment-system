package com.fedicode.intelligentrecruitments.Controller;

import com.fedicode.intelligentrecruitments.Dto.AuthResponse;
import com.fedicode.intelligentrecruitments.Dto.LoginRequest;
import com.fedicode.intelligentrecruitments.Dto.RegisterRequest;
import com.fedicode.intelligentrecruitments.Service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private AuthService authService;

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
}
