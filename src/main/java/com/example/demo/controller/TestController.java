package com.example.demo.controller;

import com.example.demo.service.email.EmailService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    private final EmailService emailService;

    public TestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/email")
    public String sendTestEmail(@RequestParam String to) {

        Map<String, Object> variables = new HashMap<>();

        variables.put("name", "Sambidhan");

        emailService.sendHtmlEmail(
                to,
                "Welcome to CloudVault",
                "email/welcome",
                variables
        );

        return "Email sent successfully.";
    }
}
