package com.example.demo.service.adminSupportQuery;


import com.example.demo.dto.AdminSupportQueryResponse;
import com.example.demo.entity.SupportQuery;
import com.example.demo.repo.SupportQueryRepository;
import com.example.demo.service.password.PasswordResetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminSupportQueryServiceImpl
        implements AdminSupportQueryService {

    private final SupportQueryRepository supportQueryRepository;
    private final PasswordResetService passwordResetService;

    public AdminSupportQueryServiceImpl(
            SupportQueryRepository supportQueryRepository,
            PasswordResetService passwordResetService) {

        this.supportQueryRepository =
                supportQueryRepository;

        this.passwordResetService =
                passwordResetService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminSupportQueryResponse> getAllQueries() {

        return supportQueryRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(query ->
                        new AdminSupportQueryResponse(

                                query.getQueryId(),

                                query.getUser().getId(),

                                query.getUser().getFullName(),

                                query.getUser().getEmail(),

                                query.getQueryType(),

                                query.getStatus(),

                                query.getQuery(),

                                query.getCreatedAt(),

                                query.getUpdatedAt()
                        )
                )
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public AdminSupportQueryResponse getQueryById(
            Long queryId) {

        return supportQueryRepository
                .findByQueryId(queryId)
                .map(query ->
                        new AdminSupportQueryResponse(

                                query.getQueryId(),

                                query.getUser().getId(),

                                query.getUser().getFullName(),

                                query.getUser().getEmail(),

                                query.getQueryType(),

                                query.getStatus(),

                                query.getQuery(),

                                query.getCreatedAt(),

                                query.getUpdatedAt()
                        )
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Support query not found: "
                                        + queryId
                        )
                );
    }

    @Override
    @Transactional
    public void sendPasswordResetLink(Long queryId) {

        SupportQuery supportQuery =
                supportQueryRepository
                        .findByQueryId(queryId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Support query not found: "
                                                + queryId
                                )
                        );

        passwordResetService.forgotPassword(
                supportQuery.getUser().getEmail()
        );
    }
}
