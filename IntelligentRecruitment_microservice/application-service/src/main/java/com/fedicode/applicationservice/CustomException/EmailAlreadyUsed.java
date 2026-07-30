package com.fedicode.applicationservice.CustomException;

public class EmailAlreadyUsed extends RuntimeException{

    public EmailAlreadyUsed(String message){
        super(message);
    }
}
