package com.mediscript.backend.doctor.entity;

import com.mediscript.backend.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 100)
    private String specialization;

    @Column(length = 200)
    private String qualification;

    @Column(length = 100, unique = true)
    private String licenseNumber;

    private Integer experience;

    private Double consultationFee;

    @Column(length = 500)
    private String clinicAddress;

    @Column(length = 1000)
    private String bio;

    public Doctor() {
    }

    public Doctor(
            User user,
            String specialization,
            String qualification,
            String licenseNumber,
            Integer experience,
            Double consultationFee,
            String clinicAddress,
            String bio
    ) {
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(Double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}