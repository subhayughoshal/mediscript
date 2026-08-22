package com.mediscript.backend.prescription.service;

import com.mediscript.backend.appointment.entity.Appointment;
import com.mediscript.backend.appointment.repository.AppointmentRepository;
import com.mediscript.backend.doctor.entity.Doctor;
import com.mediscript.backend.doctor.repository.DoctorRepository;
import com.mediscript.backend.patient.entity.Patient;
import com.mediscript.backend.patient.repository.PatientRepository;
import com.mediscript.backend.prescription.entity.Prescription;
import com.mediscript.backend.prescription.entity.PrescriptionItem;
import com.mediscript.backend.prescription.repository.PrescriptionRepository;
import com.mediscript.backend.user.entity.User;
import com.mediscript.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.mediscript.backend.prescription.dto.CreatePrescriptionRequest;

import java.time.LocalDate;
import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            UserRepository userRepository
    ) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    public Prescription createPrescription(
            String doctorEmail,
            CreatePrescriptionRequest request
    ) {

        User doctorUser = userRepository.findByEmail(doctorEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        Doctor doctor = doctorRepository.findByUser(doctorUser)
                .orElseThrow(() ->
                        new RuntimeException("Doctor profile not found")
                );

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() ->
                        new RuntimeException("Patient not found")
                );

        Appointment appointment = null;

        if (request.getAppointmentId() != null) {

            appointment = appointmentRepository
                    .findById(request.getAppointmentId())
                    .orElseThrow(() ->
                            new RuntimeException("Appointment not found")
                    );

            if (!appointment.getDoctor().getId().equals(doctor.getId())) {
                throw new RuntimeException(
                        "This appointment does not belong to you"
                );
            }

            if (!appointment.getPatient().getId().equals(patient.getId())) {
                throw new RuntimeException(
                        "This appointment does not belong to the specified patient"
                );
            }

            if (request.getPatientId() == null) {
                throw new RuntimeException("Patient ID is required");
            }

            if (prescriptionRepository.existsByAppointment(appointment)) {
                throw new RuntimeException(
                        "This appointment already has a prescription"
                );
            }
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException(
                    "Prescription must contain at least one medicine"
            );
        }

        Prescription prescription = new Prescription(
                doctor,
                patient,
                appointment,
                request.getPrescriptionDate(),
                request.getDiagnosis(),
                request.getNotes()
        );

        request.getItems().forEach(itemRequest -> {

            PrescriptionItem item = new PrescriptionItem(
                    itemRequest.getMedicineName(),
                    itemRequest.getDosage(),
                    itemRequest.getFrequency(),
                    itemRequest.getDuration(),
                    itemRequest.getInstructions()
            );

            prescription.addItem(item);
        });

        return prescriptionRepository.save(prescription);
    }

    public List<Prescription> getPatientPrescriptions(String patientEmail) {

        User user = userRepository.findByEmail(patientEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        Patient patient = patientRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Patient profile not found")
                );

        return prescriptionRepository.findByPatient(patient);
    }

    public List<Prescription> getDoctorPrescriptions(String doctorEmail) {

        User user = userRepository.findByEmail(doctorEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Doctor profile not found")
                );

        return prescriptionRepository.findByDoctor(doctor);
    }
}