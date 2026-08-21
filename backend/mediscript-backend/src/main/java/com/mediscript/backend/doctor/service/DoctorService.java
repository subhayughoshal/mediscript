package com.mediscript.backend.doctor.service;

import com.mediscript.backend.doctor.entity.Doctor;
import com.mediscript.backend.doctor.repository.DoctorRepository;
import com.mediscript.backend.user.entity.User;
import com.mediscript.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public DoctorService(
            DoctorRepository doctorRepository,
            UserRepository userRepository
    ) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    public Doctor createDoctor(
            Long userId,
            String specialization,
            String qualification,
            String licenseNumber,
            Integer experience,
            Double consultationFee,
            String clinicAddress,
            String bio
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        if (!"DOCTOR".equalsIgnoreCase(user.getRole().getName())) {
            throw new RuntimeException(
                    "Doctor profile can only be created for DOCTOR users"
            );
        }

        if (doctorRepository.existsByUser(user)) {
            throw new RuntimeException(
                    "Doctor profile already exists"
            );
        }

        if (doctorRepository.existsByLicenseNumber(licenseNumber)) {
            throw new RuntimeException(
                    "License number already registered"
            );
        }

        Doctor doctor = new Doctor(
                user,
                specialization,
                qualification,
                licenseNumber,
                experience,
                consultationFee,
                clinicAddress,
                bio
        );

        return doctorRepository.save(doctor);
    }

    public Doctor getDoctorByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        if (!"DOCTOR".equalsIgnoreCase(user.getRole().getName())) {
            throw new RuntimeException(
                    "User is not a doctor"
            );
        }

        return doctorRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Doctor profile not found"
                        )
                );
    }

    public Doctor updateDoctor(
            String email,
            String specialization,
            String qualification,
            String licenseNumber,
            Integer experience,
            Double consultationFee,
            String clinicAddress,
            String bio
    ) {

        Doctor doctor = getDoctorByEmail(email);

        if (!doctor.getLicenseNumber().equals(licenseNumber)
                && doctorRepository.existsByLicenseNumber(licenseNumber)) {
            throw new RuntimeException(
                    "License number already registered"
            );
        }

        doctor.setSpecialization(specialization);
        doctor.setQualification(qualification);
        doctor.setLicenseNumber(licenseNumber);
        doctor.setExperience(experience);
        doctor.setConsultationFee(consultationFee);
        doctor.setClinicAddress(clinicAddress);
        doctor.setBio(bio);

        return doctorRepository.save(doctor);
    }
}