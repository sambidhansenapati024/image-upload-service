package com.example.demo.entity;

import com.example.demo.enums.SupportQueryStatus;
import com.example.demo.enums.SupportQueryType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "support_queries",
        indexes = {
                @Index(
                        name = "idx_support_query_id",
                        columnList = "query_id"
                ),
                @Index(
                        name = "idx_support_query_user_id",
                        columnList = "user_id"
                )
        }
)
public class SupportQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Public query ID shown to the user.
     * Example: 10008700
     */

    @Column(
            name = "request_id",
            nullable = false,
            unique = true
    )
    private UUID requestId;

    @Column(
            name = "query_id",
            nullable = false,
            unique = true
    )
    private Long queryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(
            name = "query",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String query;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private SupportQueryStatus status;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "query_type", nullable = false, length = 30)
    private SupportQueryType queryType;

    @OneToMany(
            mappedBy = "supportQuery",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("changedAt ASC")
    private List<SupportQueryStatusHistory> statusHistory =
            new ArrayList<>();


    public SupportQuery() {
    }


    @PrePersist
    public void onCreate() {

        this.createdAt = LocalDateTime.now();

        this.updatedAt = LocalDateTime.now();

        if (this.status == null) {

            this.status = SupportQueryStatus.OPEN;

        }

    }


    @PreUpdate
    public void onUpdate() {

        this.updatedAt = LocalDateTime.now();

    }

    public List<SupportQueryStatusHistory> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(
            List<SupportQueryStatusHistory> statusHistory) {

        this.statusHistory = statusHistory;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getQueryId() {
        return queryId;
    }

    public void setQueryId(Long queryId) {
        this.queryId = queryId;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }


    public SupportQueryStatus getStatus() {
        return status;
    }

    public void setStatus(SupportQueryStatus status) {
        this.status = status;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public SupportQueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(SupportQueryType queryType) {
        this.queryType = queryType;
    }

}
