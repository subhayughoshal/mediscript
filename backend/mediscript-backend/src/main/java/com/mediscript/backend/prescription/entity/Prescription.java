package com.mediscript.backend.prescription.entity;

import com.mediscript.backend.appointment.entity.Appointment;
import com.mediscript.backend.doctor.entity.Doctor;
import com.mediscript.backend.patient.entity.Patient;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Optional: a prescription can be written without being tied
    // to a specific appointment.
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "appointment_id", nullable = true)
    private Appointment appointment;

    @Column(nullable = false)
    private LocalDate prescriptionDate;

    @Column(length = 1000)
    private String diagnosis;

    @Column(length = 1000)
    private String notes;

    @OneToMany(
            mappedBy = "prescription",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<PrescriptionItem> items = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Prescription() {
    }

    public Prescription(
            Doctor doctor,
            Patient patient,
            Appointment appointment,
            LocalDate prescriptionDate,
            String diagnosis,
            String notes
    ) {
        this.doctor = doctor;
        this.patient = patient;
        this.appointment = appointment;
        this.prescriptionDate = prescriptionDate;
        this.diagnosis = diagnosis;
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (prescriptionDate == null) {
            prescriptionDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Convenience helper to keep both sides of the
    // Prescription <-> PrescriptionItem relationship in sync.
    public void addItem(PrescriptionItem item) {
        items.add(item);
        item.setPrescription(this);
    }

    public void removeItem(PrescriptionItem item) {
        items.remove(item);
        item.setPrescription(null);
    }

    public Long getId() {
        return id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<PrescriptionItem> getItems() {
        return items;
    }

    public void setItems(List<PrescriptionItem> items) {
        this.items = items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}