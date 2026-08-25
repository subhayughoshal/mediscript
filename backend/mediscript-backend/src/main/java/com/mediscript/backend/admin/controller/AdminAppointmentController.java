package com.mediscript.backend.admin.controller;

import com.mediscript.backend.appointment.dto.AppointmentResponse;
import com.mediscript.backend.appointment.entity.Appointment;
import com.mediscript.backend.appointment.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/appointments")
public class AdminAppointmentController {

    private final AppointmentService appointmentService;

    public AdminAppointmentController(
            AppointmentService appointmentService
    ) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {

        List<AppointmentResponse> response =
                appointmentService.getAllAppointments()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(
            @PathVariable Long id
    ) {

        Appointment appointment =
                appointmentService.getAppointmentById(id);

        return ResponseEntity.ok(toResponse(appointment));
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
}