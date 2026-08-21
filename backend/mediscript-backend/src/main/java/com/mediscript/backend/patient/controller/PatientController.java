package com.mediscript.backend.patient.controller;

import com.mediscript.backend.patient.dto.CreatePatientRequest;
import com.mediscript.backend.patient.dto.PatientResponse;
import com.mediscript.backend.patient.entity.Patient;
import com.mediscript.backend.patient.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(
            @RequestBody CreatePatientRequest request
    ) {

        Patient patient = patientService.createPatient(
                request.getUserId(),
                request.getDateOfBirth(),
                request.getGender(),
                request.getBloodGroup(),
                request.getAddress(),
                request.getEmergencyContact()
        );

        PatientResponse response = new PatientResponse(
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

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}