package com.mediscript.backend.appointment.dto;

public class UpdateAppointmentStatusRequest {

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