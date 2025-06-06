package com.example.autoprint.exception;

import org.springframework.http.HttpStatus;

public class PaymentVerificationException extends RuntimeException {
    
    private final HttpStatus status;
    
    public PaymentVerificationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    
    public HttpStatus getStatus() {
        return status;
    }
}
