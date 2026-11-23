package com.mediscript.backend.patient.controller;

import com.mediscript.backend.audit.service.AuditLogService;
import com.mediscript.backend.patient.dto.CreatePatientRequest;
import com.mediscript.backend.patient.dto.PatientResponse;
import com.mediscript.backend.patient.dto.UpdatePatientRequest;
import com.mediscript.backend.patient.entity.Patient;
import com.mediscript.backend.patient.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;
    private final AuditLogService auditLogService;

    public PatientController(
            PatientService patientService,
            AuditLogService auditLogService
    ) {
        this.patientService = patientService;
        this.auditLogService = auditLogService;
    }

    // A patient creates their own profile; an admin can create one for any PATIENT user.
    // Doctors and receptionists register new patients through the walk-in endpoints.
    @PostMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<PatientResponse> createPatient(
            Authentication authentication,
            @Valid @RequestBody CreatePatientRequest request
    ) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ROLE_ADMIN".equals(authority.getAuthority())
                );

        Patient patient = patientService.createPatient(
                authentication.getName(),
                isAdmin,
                request.getUserId(),
                request.getDateOfBirth(),
                request.getGender(),
                request.getBloodGroup(),
                request.getAddress(),
                request.getEmergencyContact()
        );

        auditLogService.log(
                "PATIENT_PROFILE_CREATED",
                "PATIENT",
                patient.getId(),
                "Patient profile created"
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(patient));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientResponse> getMyProfile(
            Authentication authentication
    ) {

        Patient patient =
                patientService.getPatientByEmail(authentication.getName());

        return ResponseEntity.ok(toResponse(patient));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientResponse> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdatePatientRequest request
    ) {

        Patient patient = patientService.updateMyProfile(
                authentication.getName(),
                request.getDateOfBirth(),
                request.getGender(),
                request.getBloodGroup(),
                request.getAddress(),
                request.getEmergencyContact()
        );

        auditLogService.log(
                "PATIENT_PROFILE_UPDATED",
                "PATIENT",
                patient.getId(),
                "Patient updated own profile"
        );

        return ResponseEntity.ok(toResponse(patient));
    }

    // Doctors can only access patients they have a non-cancelled appointment with
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PatientResponse> getPatientById(
            Authentication authentication,
            @PathVariable Long id
    ) {

        Patient patient = patientService.getPatientForDoctor(
                authentication.getName(),
                id
        );

        auditLogService.log(
                "PATIENT_PROFILE_VIEWED",
                "PATIENT",
                patient.getId(),
                "Patient profile viewed by doctor"
        );

        return ResponseEntity.ok(toResponse(patient));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PatientResponse> updatePatient(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdatePatientRequest request
    ) {

        Patient patient = patientService.updatePatientForDoctor(
                authentication.getName(),
                id,
                request.getDateOfBirth(),
                request.getGender(),
                request.getBloodGroup(),
                request.getAddress(),
                request.getEmergencyContact()
        );

        auditLogService.log(
                "PATIENT_PROFILE_UPDATED",
                "PATIENT",
                patient.getId(),
                "Patient profile updated by doctor"
        );

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
