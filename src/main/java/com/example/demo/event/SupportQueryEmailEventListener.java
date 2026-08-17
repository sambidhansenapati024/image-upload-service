package com.example.demo.event;

import com.example.demo.entity.SupportQuery;
import com.example.demo.service.email.EmailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;

@Component
public class SupportQueryEmailEventListener {

    private final EmailService emailService;

    public SupportQueryEmailEventListener(
            EmailService emailService) {

        this.emailService = emailService;
    }


    @Async("emailTaskExecutor")
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleSupportQueryCreated(
            SupportQueryCreatedEvent event) {

        SupportQuery supportQuery =
                event.getSupportQuery();

        try {

            sendAdminEmail(supportQuery);

        } catch (Exception ex) {

            // Admin email failure must not
            // affect user email.
            System.err.println(
                    "Failed to send support query admin email: "
                            + supportQuery.getQueryId()
            );

            ex.printStackTrace();
        }


        try {

            sendUserConfirmationEmail(supportQuery);

        } catch (Exception ex) {

            // User email failure must not
            // affect admin email.
            System.err.println(
                    "Failed to send support query user email: "
                            + supportQuery.getQueryId()
            );

            ex.printStackTrace();
        }
    }

    private void sendAdminEmail(
            SupportQuery supportQuery) {

        Map<String, Object> variables =
                createVariables(supportQuery);

        emailService.sendHtmlEmailAsync(
                "cloudvapp@gmail.com",
                "CloudVault - New Support Query #"
                        + supportQuery.getQueryId(),
                "email/support-query-admin",
                variables
        );
    }


    private void sendUserConfirmationEmail(
            SupportQuery supportQuery) {

        Map<String, Object> variables =
                createVariables(supportQuery);

        emailService.sendHtmlEmailAsync(
                supportQuery.getUser().getEmail(),
                "CloudVault - Support Query #"
                        + supportQuery.getQueryId()
                        + " Received",
                "email/support-query-user",
                variables
        );
    }


    private Map<String, Object> createVariables(
            SupportQuery supportQuery) {

        Map<String, Object> variables =
                new HashMap<>();

        variables.put(
                "queryId",
                supportQuery.getQueryId()
        );

        variables.put(
                "userId",
                supportQuery.getUser().getId()
        );

        variables.put(
                "userName",
                supportQuery.getUser().getFullName()
        );

        variables.put(
                "userEmail",
                supportQuery.getUser().getEmail()
        );

        variables.put(
                "status",
                supportQuery.getStatus().name()
        );

        variables.put(
                "createdAt",
                supportQuery.getCreatedAt()
        );

        variables.put(
                "query",
                supportQuery.getQuery()
        );

        variables.put(
                "queryType",
                supportQuery.getQueryType().name()
        );

        return variables;
    }

}