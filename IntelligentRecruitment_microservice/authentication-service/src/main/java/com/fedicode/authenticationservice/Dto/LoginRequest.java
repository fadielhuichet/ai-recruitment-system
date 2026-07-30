package com.fedicode.authenticationservice.Dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message="email is required")
    @Email(message = "Email should be valid email")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 8,message ="Password must be at least 8 characters long")
    private String password;
}
