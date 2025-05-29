package com.email.verification.email.errors;

import lombok.Getter;

@Getter
public enum EmailException {

    private final String message;

    EmailException(String message) {
        this.message = message;
    }
}
