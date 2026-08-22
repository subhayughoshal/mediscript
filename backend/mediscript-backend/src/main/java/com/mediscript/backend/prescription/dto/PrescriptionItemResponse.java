package com.mediscript.backend.prescription.dto;

public class PrescriptionItemResponse {

    private Long id;

    private String medicineName;
    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;

    public PrescriptionItemResponse(
            Long id,
            String medicineName,
            String dosage,
            String frequency,
            String duration,
            String instructions
    ) {
        this.id = id;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.instructions = instructions;
    }

    public Long getId() {
        return id;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public String getDosage() {
        return dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getDuration() {
        return duration;
    }

    public String getInstructions() {
        return instructions;
    }
}