package com.example.demo.dto;

import com.example.demo.enums.SupportQueryStatus;
import com.example.demo.enums.SupportQueryType;

import java.time.LocalDateTime;
import java.util.List;

public class SupportQueryDetailsResponse {

    private Long queryId;

    private SupportQueryType queryType;

    private SupportQueryStatus status;

    private String query;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<SupportQueryTimelineResponse> timeline;

    public SupportQueryDetailsResponse() {
    }

    public SupportQueryDetailsResponse(
            Long queryId,
            SupportQueryType queryType,
            SupportQueryStatus status,
            String query,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            List<SupportQueryTimelineResponse> timeline) {

        this.queryId = queryId;
        this.queryType = queryType;
        this.status = status;
        this.query = query;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.timeline = timeline;
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

    public List<SupportQueryTimelineResponse> getTimeline() {
        return timeline;
    }

    public void setTimeline(
            List<SupportQueryTimelineResponse> timeline) {

        this.timeline = timeline;
    }
}
