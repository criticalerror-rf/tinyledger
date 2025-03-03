package com.rehan.tinyledger.adapter.web;

public enum ErrorMessages {
    INVALID_INPUT("The request does not have the appropriate payload."),
    INSUFFICIENT_FUNDS("The withdrawal failed due to insufficient funds"),
    NOT_FOUND("The requested resource cannot be found");

    private final String message;
    
    ErrorMessages(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}