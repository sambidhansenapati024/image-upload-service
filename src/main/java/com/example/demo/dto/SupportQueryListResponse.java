package com.example.demo.dto;

import com.example.demo.enums.SupportQueryStatus;
import com.example.demo.enums.SupportQueryType;

import java.time.LocalDateTime;

public class SupportQueryListResponse {

    private Long queryId;

    private SupportQueryType queryType;

    private SupportQueryStatus status;

    private String query;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    public SupportQueryListResponse() {
    }


    public SupportQueryListResponse(
            Long queryId,
            SupportQueryType queryType,
            SupportQueryStatus status,
            String query,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.queryId = queryId;
        this.queryType = queryType;
        this.status = status;
        this.query = query;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public Long getQueryId() {
        return queryId;
    }

    public void setQueryId(Long queryId) {
        this.queryId = queryId;
    }


    public SupportQueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(
            SupportQueryType queryType) {

        this.queryType = queryType;
    }


    public SupportQueryStatus getStatus() {
        return status;
    }

    public void setStatus(
            SupportQueryStatus status) {

        this.status = status;
    }


    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {

        this.updatedAt = updatedAt;
    }
}