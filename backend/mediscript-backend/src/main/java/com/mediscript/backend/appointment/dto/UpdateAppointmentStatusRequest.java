package com.mediscript.backend.appointment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateAppointmentStatusRequest {

    @NotBlank(message = "Status is required")
    @Pattern(
            regexp = "CONFIRMED|CANCELLED|COMPLETED",
            message = "Status must be CONFIRMED, CANCELLED, or COMPLETED"
    )
    private String status;

    public UpdateAppointmentStatusRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}