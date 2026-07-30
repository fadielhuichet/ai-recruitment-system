package com.fedicode.authenticationservice.Exception;

import com.fedicode.authenticationservice.CustomException.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {



    @ExceptionHandler(IsNotWorkEmail.class)
    public ResponseEntity<?> handleIsNotWorkEmail(IsNotWorkEmail ex){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error","IS_NOT_WORK_EMAIL","message",ex.getMessage()));
    }
    @ExceptionHandler(EmailAlreadyExist.class)
    public ResponseEntity<?> handleEmailAlreadyExist(EmailAlreadyExist ex){
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("error","EMAIL_ALREADY_EXIST","message", ex.getMessage()));
    }
    @ExceptionHandler(AccountSuspended.class)
    public ResponseEntity<?>handleAccountSuspended(AccountSuspended ex){
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("error","Account Suspended","message",ex.getMessage()));
    }
    @ExceptionHandler(EmailNotExistException.class)
    public ResponseEntity<?>handleEmailNotExist(EmailNotExistException ex){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "EMAIL_NOT_FOUND", "message", ex.getMessage()));
    }

    @ExceptionHandler(InvalidCodeException.class)
    public ResponseEntity<?> handleInvalidCode(InvalidCodeException ex){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "INVALID_CODE", "message", ex.getMessage()));
    }
    @ExceptionHandler(CodeExpiredException.class)
    public ResponseEntity<?> handleCodeExpired(CodeExpiredException ex){
        return ResponseEntity
                .status(HttpStatus.GONE)
                .body(Map.of("error", "CODE_EXPIRED", "message", ex.getMessage()));
    }
}
