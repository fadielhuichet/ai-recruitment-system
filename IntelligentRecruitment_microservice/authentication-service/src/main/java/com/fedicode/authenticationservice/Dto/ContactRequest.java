package com.fedicode.authenticationservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactRequest {
    String firstName;
    String email;
    String subject;
    String message;

}
