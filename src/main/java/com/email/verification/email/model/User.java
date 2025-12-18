package com.email.verification.email.model;

import lombok.Data;

import java.util.UUID;

@Data
public class User {
    private UUID id;
    private String name;
}
