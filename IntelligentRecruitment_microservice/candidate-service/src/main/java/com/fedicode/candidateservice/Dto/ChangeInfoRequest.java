package com.fedicode.candidateservice.Dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class ChangeInfoRequest {
    String firstName;
    String lastName;
    String email;
    LocalDate DateOfBirth;
    String country;
    String phone;


}
