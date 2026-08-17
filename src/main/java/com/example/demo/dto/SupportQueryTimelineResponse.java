package com.example.demo.dto;

import com.example.demo.enums.SupportQueryHistoryStatus;

import java.time.LocalDateTime;

public class SupportQueryTimelineResponse {

    private SupportQueryHistoryStatus status;

    private LocalDateTime changedAt;

    public SupportQueryTimelineResponse() {
    }

    public SupportQueryTimelineResponse(
            SupportQueryHistoryStatus status,
            LocalDateTime changedAt) {

        this.status = status;
        this.changedAt = changedAt;
    }

    public SupportQueryHistoryStatus getStatus() {
        return status;
    }

    public void setStatus(
            SupportQueryHistoryStatus status) {

        this.status = status;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(
            LocalDateTime changedAt) {

        this.changedAt = changedAt;
    }
}