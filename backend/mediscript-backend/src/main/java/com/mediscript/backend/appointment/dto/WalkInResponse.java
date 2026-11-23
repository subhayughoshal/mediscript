package com.mediscript.backend.appointment.dto;

public class WalkInResponse {

    private Long patientId;
    private String patientName;
    private String patientEmail;

    // true when this request created a brand-new login account for the patient
    private boolean newAccountCreated;

    // true when this request created the patient's profile
    private boolean newProfileCreated;

    private AppointmentResponse appointment;

    public WalkInResponse(
            Long patientId,
            String patientName,
            String patientEmail,
            boolean newAccountCreated,
            boolean newProfileCreated,
            AppointmentResponse appointment
    ) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.newAccountCreated = newAccountCreated;
        this.newProfileCreated = newProfileCreated;
        this.appointment = appointment;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public boolean isNewAccountCreated() {
        return newAccountCreated;
    }

    public boolean isNewProfileCreated() {
        return newProfileCreated;
    }

    public AppointmentResponse getAppointment() {
        return appointment;
    }
}
