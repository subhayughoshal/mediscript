package com.mediscript.backend.admin.controller;

import com.mediscript.backend.prescription.dto.PrescriptionItemResponse;
import com.mediscript.backend.prescription.dto.PrescriptionResponse;
import com.mediscript.backend.prescription.entity.Prescription;
import com.mediscript.backend.prescription.service.PrescriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/prescriptions")
public class AdminPrescriptionController {

    private final PrescriptionService prescriptionService;

    public AdminPrescriptionController(
            PrescriptionService prescriptionService
    ) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping
    public ResponseEntity<List<PrescriptionResponse>> getAllPrescriptions() {

        List<PrescriptionResponse> response =
                prescriptionService.getAllPrescriptions()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionResponse> getPrescriptionById(
            @PathVariable Long id
    ) {

        Prescription prescription =
                prescriptionService.getPrescriptionById(id);

        return ResponseEntity.ok(toResponse(prescription));
    }

    private PrescriptionResponse toResponse(
            Prescription prescription
    ) {

        List<PrescriptionItemResponse> items =
                prescription.getItems()
                        .stream()
                        .map(item ->
                                new PrescriptionItemResponse(
                                        item.getId(),
                                        item.getMedicineName(),
                                        item.getDosage(),
                                        item.getFrequency(),
                                        item.getDuration(),
                                        item.getInstructions()
                                )
                        )
                        .toList();

        Long appointmentId =
                prescription.getAppointment() != null
                        ? prescription.getAppointment().getId()
                        : null;

        return new PrescriptionResponse(
                prescription.getId(),

                prescription.getPatient().getId(),
                prescription.getPatient().getUser().getName(),

                prescription.getDoctor().getId(),
                prescription.getDoctor().getUser().getName(),
                prescription.getDoctor().getSpecialization(),

                appointmentId,

                prescription.getPrescriptionDate(),
                prescription.getDiagnosis(),
                prescription.getNotes(),

                items,

                prescription.getCreatedAt(),
                prescription.getUpdatedAt()
        );
    }
}