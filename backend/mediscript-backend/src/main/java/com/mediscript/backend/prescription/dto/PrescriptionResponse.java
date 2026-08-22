package com.mediscript.backend.prescription.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PrescriptionResponse {

    private Long id;

    private Long patientId;
    private String patientName;

    private Long doctorId;
    private String doctorName;
    private String doctorSpecialization;

    private Long appointmentId;

    private LocalDate prescriptionDate;

    private String diagnosis;

    private String notes;

    private List<PrescriptionItemResponse> items;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PrescriptionResponse(
            Long id,
            Long patientId,
            String patientName,
            Long doctorId,
            String doctorName,
            String doctorSpecialization,
            Long appointmentId,
            LocalDate prescriptionDate,
            String diagnosis,
            String notes,
            List<PrescriptionItemResponse> items,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorSpecialization = doctorSpecialization;
        this.appointmentId = appointmentId;
        this.prescriptionDate = prescriptionDate;
        this.diagnosis = diagnosis;
        this.notes = notes;
        this.items = items;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDoctorSpecialization() {
        return doctorSpecialization;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getNotes() {
        return notes;
    }

    public List<PrescriptionItemResponse> getItems() {
        return items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}