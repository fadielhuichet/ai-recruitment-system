package com.fedicode.applicationservice.Exception;

import com.fedicode.applicationservice.CustomException.EmailAlreadyUsed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyUsed.class)
    public ResponseEntity<?> handleEmailAlreadyUsed(EmailAlreadyUsed ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error","Email already used","message",ex.getMessage()));

    }
}
