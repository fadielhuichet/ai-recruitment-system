package com.fedicode.authenticationservice.CustomException;

public class AccountSuspended extends RuntimeException {
    public AccountSuspended(String message) {
        super(message);
    }
}
