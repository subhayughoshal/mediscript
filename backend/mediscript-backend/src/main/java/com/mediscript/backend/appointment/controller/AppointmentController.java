package com.mediscript.backend.appointment.controller;

import com.mediscript.backend.appointment.dto.AppointmentResponse;
import com.mediscript.backend.appointment.dto.CreateAppointmentRequest;
import com.mediscript.backend.appointment.entity.Appointment;
import com.mediscript.backend.appointment.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.mediscript.backend.appointment.dto.UpdateAppointmentStatusRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import com.mediscript.backend.audit.service.AuditLogService;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AuditLogService auditLogService;

    public AppointmentController(
            AppointmentService appointmentService,
            AuditLogService auditLogService
    ) {
        this.appointmentService = appointmentService;
        this.auditLogService = auditLogService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<AppointmentResponse> createAppointment(
            Authentication authentication,
            @Valid @RequestBody CreateAppointmentRequest request
    ) {

        Appointment appointment =
                appointmentService.createAppointment(
                        authentication.getName(),
                        request.getDoctorId(),
                        request.getAppointmentDate(),
                        request.getAppointmentTime(),
                        request.getReason()
                );

        auditLogService.log(
                "APPOINTMENT_CREATED",
                "APPOINTMENT",
                appointment.getId(),
                "Appointment created with doctor " + appointment.getDoctor().getId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(appointment));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(
            Authentication authentication
    ) {

        List<AppointmentResponse> response =
                appointmentService
                        .getPatientAppointments(authentication.getName())
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<AppointmentResponse>> getDoctorAppointments(
            Authentication authentication
    ) {

        List<AppointmentResponse> response =
                appointmentService
                        .getDoctorAppointments(authentication.getName())
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    private AppointmentResponse toResponse(
            Appointment appointment
    ) {

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

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AppointmentResponse> updateAppointmentStatus(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentStatusRequest request
    ) {

        Appointment currentAppointment =
                appointmentService.getAppointmentById(id);

        String oldStatus = currentAppointment.getStatus();

        Appointment appointment =
                appointmentService.updateAppointmentStatus(
                        authentication.getName(),
                        id,
                        request.getStatus()
                );

        auditLogService.log(
                "APPOINTMENT_STATUS_CHANGED",
                "APPOINTMENT",
                appointment.getId(),
                "Changed appointment status from "
                        + oldStatus
                        + " to "
                        + appointment.getStatus()
        );

        return ResponseEntity.ok(toResponse(appointment));
    }

}