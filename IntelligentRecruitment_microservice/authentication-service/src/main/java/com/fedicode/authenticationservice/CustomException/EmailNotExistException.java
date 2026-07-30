package com.fedicode.authenticationservice.CustomException;


public class EmailNotExistException extends RuntimeException{
    public EmailNotExistException(String message){
        super(message);
    }
}
