package com.fedicode.applicationservice.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Candidate {

    private int id;
    private String cvFilePath;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate DateOfBirth;
}
