package com.mediscript.backend.doctor.controller;

import com.mediscript.backend.doctor.dto.CreateDoctorRequest;
import com.mediscript.backend.doctor.dto.DoctorResponse;
import com.mediscript.backend.doctor.entity.Doctor;
import com.mediscript.backend.doctor.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.mediscript.backend.doctor.dto.DoctorSummaryResponse;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public ResponseEntity<DoctorResponse> createDoctor(
            @RequestBody CreateDoctorRequest request
    ) {

        Doctor doctor = doctorService.createDoctor(
                request.getUserId(),
                request.getSpecialization(),
                request.getQualification(),
                request.getLicenseNumber(),
                request.getExperience(),
                request.getConsultationFee(),
                request.getClinicAddress(),
                request.getBio()
        );

        DoctorResponse response = new DoctorResponse(
                doctor.getId(),
                doctor.getUser().getId(),
                doctor.getUser().getName(),
                doctor.getUser().getEmail(),
                doctor.getUser().getPhone(),
                doctor.getSpecialization(),
                doctor.getQualification(),
                doctor.getLicenseNumber(),
                doctor.getExperience(),
                doctor.getConsultationFee(),
                doctor.getClinicAddress(),
                doctor.getBio()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<DoctorResponse> getMyProfile(
            Authentication authentication
    ) {

        Doctor doctor = doctorService.getDoctorByEmail(
                authentication.getName()
        );

        DoctorResponse response = new DoctorResponse(
                doctor.getId(),
                doctor.getUser().getId(),
                doctor.getUser().getName(),
                doctor.getUser().getEmail(),
                doctor.getUser().getPhone(),
                doctor.getSpecialization(),
                doctor.getQualification(),
                doctor.getLicenseNumber(),
                doctor.getExperience(),
                doctor.getConsultationFee(),
                doctor.getClinicAddress(),
                doctor.getBio()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<DoctorResponse> updateMyProfile(
            Authentication authentication,
            @RequestBody CreateDoctorRequest request
    ) {

        Doctor doctor = doctorService.updateDoctor(
                authentication.getName(),
                request.getSpecialization(),
                request.getQualification(),
                request.getLicenseNumber(),
                request.getExperience(),
                request.getConsultationFee(),
                request.getClinicAddress(),
                request.getBio()
        );

        DoctorResponse response = new DoctorResponse(
                doctor.getId(),
                doctor.getUser().getId(),
                doctor.getUser().getName(),
                doctor.getUser().getEmail(),
                doctor.getUser().getPhone(),
                doctor.getSpecialization(),
                doctor.getQualification(),
                doctor.getLicenseNumber(),
                doctor.getExperience(),
                doctor.getConsultationFee(),
                doctor.getClinicAddress(),
                doctor.getBio()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DoctorSummaryResponse>> getAllDoctors() {

        List<Doctor> doctors = doctorService.getAllDoctors();

        List<DoctorSummaryResponse> response = doctors.stream()
                .map(doctor -> new DoctorSummaryResponse(
                        doctor.getId(),
                        doctor.getUser().getName(),
                        doctor.getSpecialization(),
                        doctor.getQualification(),
                        doctor.getExperience(),
                        doctor.getConsultationFee(),
                        doctor.getClinicAddress(),
                        doctor.getUser().getPhone()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorSummaryResponse> getDoctorById(
            @PathVariable Long id
    ) {

        Doctor doctor = doctorService.getDoctorById(id);

        DoctorSummaryResponse response = new DoctorSummaryResponse(
                doctor.getId(),
                doctor.getUser().getName(),
                doctor.getSpecialization(),
                doctor.getQualification(),
                doctor.getExperience(),
                doctor.getConsultationFee(),
                doctor.getClinicAddress(),
                doctor.getUser().getPhone()
        );

        return ResponseEntity.ok(response);
    }
}