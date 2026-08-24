package com.mediscript.backend.admin.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateUserStatusRequest {

    @NotBlank(message = "Status is required")
    private String status;

    public UpdateUserStatusRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}