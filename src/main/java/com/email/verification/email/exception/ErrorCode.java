package com.email.verification.email.exception;

public enum ErrorCode {
    MX_RECORDS_NOT_FOUND("Mx records not found.");

    public String getMessage() {
        return message;
    }

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
