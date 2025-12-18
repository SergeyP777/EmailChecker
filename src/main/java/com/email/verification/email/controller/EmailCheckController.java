package com.email.verification.email.controller;

import com.email.verification.email.dto.EmailInfoDto;
import com.email.verification.email.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email")
public class EmailCheckController {
    private final EmailService emailService;

    public EmailCheckController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/{emailInfo}")
    public EmailInfoDto getEmailInfo(@PathVariable String emailInfo) {
       return emailService.getInfoAboutEmail(emailInfo);
    }
}
