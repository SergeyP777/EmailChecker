package com.email.verification.email.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CorrectionDomainServiceTest {

    @Test
    void checkCorrectionDomainNamesTest() {
        DNSMXCheckerService dnsmxCheckerService = new DNSMXCheckerService();
        assertEquals(0, dnsmxCheckerService.rateDifferentDomainNames(
                "gmail.com",
                "gmail.com"));
    }

    @Test
    void checkIncorrectionDomainNamesTest() {
        DNSMXCheckerService dnsmxCheckerService = new DNSMXCheckerService();
        assertEquals(1, dnsmxCheckerService.rateDifferentDomainNames(
                "gaail.com",
                "gmail.com"));
    }
}