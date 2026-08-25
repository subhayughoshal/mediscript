package com.mediscript.backend.admin.controller;

import com.mediscript.backend.patient.dto.PatientResponse;
import com.mediscript.backend.patient.entity.Patient;
import com.mediscript.backend.patient.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/patients")
public class AdminPatientController {

    private final PatientService patientService;

    public AdminPatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAllPatients() {

        List<PatientResponse> response =
                patientService.getAllPatients()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(
            @PathVariable Long id
    ) {

        Patient patient = patientService.getPatientById(id);

        return ResponseEntity.ok(toResponse(patient));
    }

    private PatientResponse toResponse(Patient patient) {

        return new PatientResponse(
                patient.getId(),
                patient.getUser().getId(),
                patient.getUser().getName(),
                patient.getUser().getEmail(),
                patient.getUser().getPhone(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getBloodGroup(),
                patient.getAddress(),
                patient.getEmergencyContact()
        );
    }
}