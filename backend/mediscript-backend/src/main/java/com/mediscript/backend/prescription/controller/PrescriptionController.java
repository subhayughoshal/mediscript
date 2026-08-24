package com.mediscript.backend.prescription.controller;

import com.mediscript.backend.prescription.dto.CreatePrescriptionRequest;
import com.mediscript.backend.prescription.dto.PrescriptionItemResponse;
import com.mediscript.backend.prescription.dto.PrescriptionResponse;
import com.mediscript.backend.prescription.entity.Prescription;
import com.mediscript.backend.prescription.service.PrescriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(
            PrescriptionService prescriptionService
    ) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    public ResponseEntity<PrescriptionResponse> createPrescription(
            Authentication authentication,
            @Valid @RequestBody CreatePrescriptionRequest request
    ) {

        Prescription prescription =
                prescriptionService.createPrescription(
                        authentication.getName(),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(prescription));
    }

    @GetMapping("/my")
    public ResponseEntity<List<PrescriptionResponse>> getMyPrescriptions(
            Authentication authentication
    ) {

        List<PrescriptionResponse> response =
                prescriptionService
                        .getPatientPrescriptions(authentication.getName())
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctor")
    public ResponseEntity<List<PrescriptionResponse>> getDoctorPrescriptions(
            Authentication authentication
    ) {

        List<PrescriptionResponse> response =
                prescriptionService
                        .getDoctorPrescriptions(authentication.getName())
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
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