package com.fedicode.authenticationservice.CustomException;

public class CodeExpiredException extends RuntimeException{

    public CodeExpiredException(String message){
        super(message);
    }
}
