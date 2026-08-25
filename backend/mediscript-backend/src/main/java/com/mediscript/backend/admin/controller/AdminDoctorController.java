package com.mediscript.backend.admin.controller;

import com.mediscript.backend.doctor.dto.DoctorSummaryResponse;
import com.mediscript.backend.doctor.entity.Doctor;
import com.mediscript.backend.doctor.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/doctors")
public class AdminDoctorController {

    private final DoctorService doctorService;

    public AdminDoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public ResponseEntity<List<DoctorSummaryResponse>> getAllDoctors() {

        List<DoctorSummaryResponse> response =
                doctorService.getAllDoctors()
                        .stream()
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