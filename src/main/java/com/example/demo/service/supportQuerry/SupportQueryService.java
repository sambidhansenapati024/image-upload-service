package com.example.demo.service.supportQuerry;

import com.example.demo.dto.SupportQueryDetailsResponse;
import com.example.demo.dto.SupportQueryListResponse;
import com.example.demo.dto.SupportQueryRequest;
import com.example.demo.dto.SupportQueryResponse;

import java.util.List;

public interface SupportQueryService {

    SupportQueryResponse createQuery(
            SupportQueryRequest request
    );

    List<SupportQueryListResponse> getMyQueries();

    SupportQueryDetailsResponse getMyQueryDetails(Long queryId);

}