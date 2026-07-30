package com.fedicode.recruiterservice.Dto;

import lombok.Data;

@Data
public class ChangeInfoRequest {
    String firstName;
    String lastName;
    String companyName;
    String email;
    String phone;

}
