package com.example.demo.dto;

import com.example.demo.enums.SupportQueryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class SupportQueryRequest {

    @NotBlank(message = "Query cannot be empty")
    @Size(
            min = 10,
            max = 1000,
            message = "Query must be between 10 and 1000 characters"
    )
    private String query;

    @NotNull(message = "Request ID is required")
    private UUID requestId;

    @NotNull(message = "Query type is required")
    private SupportQueryType queryType;


    public SupportQueryRequest() {
    }


    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public SupportQueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(SupportQueryType queryType) {
        this.queryType = queryType;
    }


    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

}