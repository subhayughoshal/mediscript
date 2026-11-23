package com.mediscript.backend.appointment.controller;

import com.mediscript.backend.appointment.dto.AppointmentResponse;
import com.mediscript.backend.appointment.dto.WalkInRequest;
import com.mediscript.backend.appointment.dto.WalkInResponse;
import com.mediscript.backend.appointment.entity.Appointment;
import com.mediscript.backend.appointment.service.WalkInService;
import com.mediscript.backend.appointment.service.WalkInService.WalkInResult;
import com.mediscript.backend.audit.service.AuditLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class WalkInController {

    private final WalkInService walkInService;
    private final AuditLogService auditLogService;

    public WalkInController(
            WalkInService walkInService,
            AuditLogService auditLogService
    ) {
        this.walkInService = walkInService;
        this.auditLogService = auditLogService;
    }

    // Receptionist (or admin) registers a walk-in and picks the doctor
    @PostMapping("/reception/walk-ins")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<WalkInResponse> registerWalkInAtReception(
            @Valid @RequestBody WalkInRequest request
    ) {

        WalkInResult result = walkInService.registerWalkInForDoctorId(
                request,
                request.getDoctorId()
        );

        return buildResponse(result);
    }

    // A doctor registers a walk-in for themselves (doctorId in the body is ignored)
    @PostMapping("/doctors/me/walk-ins")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<WalkInResponse> registerWalkInAsDoctor(
            Authentication authentication,
            @Valid @RequestBody WalkInRequest request
    ) {

        WalkInResult result = walkInService.registerWalkInForDoctorEmail(
                request,
                authentication.getName()
        );

        return buildResponse(result);
    }

    private ResponseEntity<WalkInResponse> buildResponse(WalkInResult result) {

        Appointment appointment = result.appointment();

        // Audit entries contain ids only, never clinical or personal details
        if (result.newAccountCreated()) {
            auditLogService.log(
                    "USER_CREATED",
                    "USER",
                    appointment.getPatient().getUser().getId(),
                    "Patient account created during walk-in registration"
            );
        }

        if (result.newProfileCreated()) {
            auditLogService.log(
                    "PATIENT_PROFILE_CREATED",
                    "PATIENT",
                    appointment.getPatient().getId(),
                    "Patient profile created during walk-in registration"
            );
        }

        auditLogService.log(
                "WALK_IN_APPOINTMENT_CREATED",
                "APPOINTMENT",
                appointment.getId(),
                "Walk-in appointment created with doctor "
                        + appointment.getDoctor().getId()
        );

        WalkInResponse response = new WalkInResponse(
                appointment.getPatient().getId(),
                appointment.getPatient().getUser().getName(),
                appointment.getPatient().getUser().getEmail(),
                result.newAccountCreated(),
                result.newProfileCreated(),
                toAppointmentResponse(appointment)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    private AppointmentResponse toAppointmentResponse(Appointment appointment) {

        return new AppointmentResponse(
                appointment.getId(),

                appointment.getPatient().getId(),
                appointment.getPatient().getUser().getName(),

                appointment.getDoctor().getId(),
                appointment.getDoctor().getUser().getName(),
                appointment.getDoctor().getSpecialization(),

                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),

                appointment.getReason(),
                appointment.getStatus(),
                appointment.getNotes(),

                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }
}
