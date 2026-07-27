package com.example.demo.service.email;

import java.util.Map;

public interface EmailService {

    void sendEmail(
            String to,
            String subject,
            String body
    );

    void sendHtmlEmail(
            String to,
            String subject,
            String template,
            Map<String, Object> variables
    );
}
