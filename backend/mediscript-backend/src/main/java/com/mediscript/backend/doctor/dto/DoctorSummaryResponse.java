package com.mediscript.backend.doctor.dto;

public class DoctorSummaryResponse {

    private Long id;
    private String name;
    private String specialization;
    private String qualification;
    private Integer experience;
    private Double consultationFee;
    private String clinicAddress;
    private String phone;

    public DoctorSummaryResponse(
            Long id,
            String name,
            String specialization,
            String qualification,
            Integer experience,
            Double consultationFee,
            String clinicAddress,
            String phone
    ) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experience = experience;
        this.consultationFee = consultationFee;
        this.clinicAddress = clinicAddress;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getQualification() {
        return qualification;
    }

    public Integer getExperience() {
        return experience;
    }

    public Double getConsultationFee() {
        return consultationFee;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public String getPhone() {
        return phone;
    }
}