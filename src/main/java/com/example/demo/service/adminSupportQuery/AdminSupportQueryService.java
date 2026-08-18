package com.example.demo.service.adminSupportQuery;

import com.example.demo.dto.AdminSupportQueryResponse;

import java.util.List;

public interface AdminSupportQueryService {

    List<AdminSupportQueryResponse> getAllQueries();

    AdminSupportQueryResponse getQueryById(Long queryId);

    void sendPasswordResetLink(Long queryId);

}
