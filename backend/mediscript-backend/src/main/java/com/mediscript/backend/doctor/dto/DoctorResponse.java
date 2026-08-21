package com.mediscript.backend.doctor.dto;

public class DoctorResponse {

    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String specialization;
    private String qualification;
    private String licenseNumber;
    private Integer experience;
    private Double consultationFee;
    private String clinicAddress;
    private String bio;

    public DoctorResponse(
            Long id,
            Long userId,
            String name,
            String email,
            String phone,
            String specialization,
            String qualification,
            String licenseNumber,
            Integer experience,
            Double consultationFee,
            String clinicAddress,
            String bio
    ) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.qualification = qualification;
        this.licenseNumber = licenseNumber;
        this.experience = experience;
        this.consultationFee = consultationFee;
        this.clinicAddress = clinicAddress;
        this.bio = bio;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getQualification() {
        return qualification;
    }

    public String getLicenseNumber() {
        return licenseNumber;
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

    public String getBio() {
        return bio;
    }
}