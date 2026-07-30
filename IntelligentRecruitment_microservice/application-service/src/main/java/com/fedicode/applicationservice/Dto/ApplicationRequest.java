package com.fedicode.applicationservice.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ApplicationRequest {

    @NotBlank
    String candidateFirstName;
    @NotBlank
    String candidateLastName;
    @NotBlank
    String candidateEmail;
    @NotBlank
    String candidatePhone;
    @NotBlank
    private MultipartFile cv;


}
