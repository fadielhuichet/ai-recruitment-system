package com.fedicode.authenticationservice.CustomException;

public class IsNotWorkEmail extends RuntimeException {
    public IsNotWorkEmail(String message) {
        super(message);
    }
}
