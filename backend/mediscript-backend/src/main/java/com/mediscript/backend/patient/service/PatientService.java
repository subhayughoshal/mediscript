package com.mediscript.backend.patient.service;

import com.mediscript.backend.patient.entity.Patient;
import com.mediscript.backend.patient.repository.PatientRepository;
import com.mediscript.backend.user.entity.User;
import com.mediscript.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public PatientService(
            PatientRepository patientRepository,
            UserRepository userRepository
    ) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    public Patient createPatient(
            Long userId,
            LocalDate dateOfBirth,
            String gender,
            String bloodGroup,
            String address,
            String emergencyContact
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        if (!"PATIENT".equalsIgnoreCase(user.getRole().getName())) {
            throw new RuntimeException(
                    "Patient profile can only be created for PATIENT users"
            );
        }

        if (patientRepository.existsByUser(user)) {
            throw new RuntimeException(
                    "Patient profile already exists"
            );
        }

        Patient patient = new Patient(
                user,
                dateOfBirth,
                gender,
                bloodGroup,
                address,
                emergencyContact
        );

        return patientRepository.save(patient);
    }
}