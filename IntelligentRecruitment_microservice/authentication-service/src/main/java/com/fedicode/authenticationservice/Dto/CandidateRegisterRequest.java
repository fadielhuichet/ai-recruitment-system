package com.fedicode.authenticationservice.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CandidateRegisterRequest {

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(
            min = 6,
            message = "Le mot de passe doit contenir au moins 6 caractères"
    )
    private String password;

    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;
    @NotBlank(message = "Le pays est obligatoire")
    private String country;

    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateOfBirth;


    @NotBlank(message = "Le numero de telephone est obligatoire")
    private String phone;

    @NotBlank(message = "Le Cv est obligatoire")
    private MultipartFile cv;
}