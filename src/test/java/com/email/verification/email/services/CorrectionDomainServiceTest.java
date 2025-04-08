package com.email.verification.email.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CorrectionDomainServiceTest {

    @Test
    void checkCorrectionDomainNamesTest() {
        EmailService emailService = new EmailService();
        assertEquals(0, emailService.rateDifferentDomainNames(
                "gmail.com",
                "gmail.com"));
    }

    @Test
    void checkIncorrectionDomainNamesTest() {
        EmailService emailService = new EmailService();
        assertEquals(1, emailService.rateDifferentDomainNames(
                "gaail.com",
                "gmail.com"));
    }
}