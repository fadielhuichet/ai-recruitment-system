package com.fedicode.authenticationservice.CustomException;

public class InvalidCodeException extends RuntimeException{
    public InvalidCodeException(String message){
        super(message);
    }
}
