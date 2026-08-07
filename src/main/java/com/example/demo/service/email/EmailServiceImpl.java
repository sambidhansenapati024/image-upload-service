package com.example.demo.service.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.context.Context;
import org.springframework.scheduling.annotation.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    public EmailServiceImpl(
            JavaMailSender mailSender,
            TemplateEngine templateEngine) {

        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendEmail(
            String to,
            String subject,
            String body) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);

        message.setSubject(subject);

        message.setText(body);

        mailSender.send(message);

    }

    @Override
    public void sendHtmlEmail(
            String to,
            String subject,
            String template,
            Map<String, Object> variables) {

        try {

            Context context = new Context();

            variables.forEach(context::setVariable);

            String html = templateEngine.process(
                    template,
                    context
            );

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(to);

            helper.setSubject(subject);

            helper.setText(html, true);

            mailSender.send(message);

        } catch (MessagingException e) {

            throw new RuntimeException("Failed to send email", e);

        }

    }

    @Async("emailTaskExecutor")
    @Override
    public void sendHtmlEmailAsync(
            String to,
            String subject,
            String template,
            Map<String, Object> variables) {

        try {

            sendHtmlEmail(
                    to,
                    subject,
                    template,
                    variables
            );

        } catch (Exception ex) {

            log.error(
                    "Failed to send email to {}",
                    to,
                    ex
            );

        }

    }

}
