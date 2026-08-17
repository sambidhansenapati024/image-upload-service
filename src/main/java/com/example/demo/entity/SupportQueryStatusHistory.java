package com.example.demo.entity;

import com.example.demo.enums.SupportQueryHistoryStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_query_status_history")
public class SupportQueryStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "support_query_id",
            nullable = false
    )
    private SupportQuery supportQuery;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private SupportQueryHistoryStatus status;

    @Column(
            name = "changed_at",
            nullable = false
    )
    private LocalDateTime changedAt;

    public SupportQueryStatusHistory() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SupportQuery getSupportQuery() {
        return supportQuery;
    }

    public void setSupportQuery(
            SupportQuery supportQuery) {

        this.supportQuery = supportQuery;
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