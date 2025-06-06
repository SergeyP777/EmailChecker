package com.email.verification.email.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    MX_RECORDS_NOT_FOUND("Mx records not found.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
