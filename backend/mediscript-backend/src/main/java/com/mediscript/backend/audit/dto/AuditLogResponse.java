package com.mediscript.backend.audit.dto;

import java.time.LocalDateTime;

public class AuditLogResponse {

    private Long id;
    private Long actorUserId;
    private String actorEmail;
    private String action;
    private String resourceType;
    private Long resourceId;
    private String details;
    private LocalDateTime createdAt;

    public AuditLogResponse(
            Long id,
            Long actorUserId,
            String actorEmail,
            String action,
            String resourceType,
            Long resourceId,
            String details,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.actorUserId = actorUserId;
        this.actorEmail = actorEmail;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.details = details;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getActorUserId() {
        return actorUserId;
    }

    public String getActorEmail() {
        return actorEmail;
    }

    public String getAction() {
        return action;
    }

    public String getResourceType() {
        return resourceType;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}