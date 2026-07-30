package com.fedicode.jobservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recruiter {
    private  int id;

    private String email;
    private String companyName;
    private String firstName;
    private String lastName;
    private String phone;
}
