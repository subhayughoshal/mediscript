package com.mediscript.backend.appointment.service;

import com.mediscript.backend.appointment.entity.Appointment;
import com.mediscript.backend.appointment.repository.AppointmentRepository;
import com.mediscript.backend.doctor.entity.Doctor;
import com.mediscript.backend.doctor.repository.DoctorRepository;
import com.mediscript.backend.patient.entity.Patient;
import com.mediscript.backend.patient.repository.PatientRepository;
import com.mediscript.backend.user.entity.User;
import com.mediscript.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.mediscript.backend.exception.BadRequestException;
import com.mediscript.backend.exception.ConflictException;
import com.mediscript.backend.exception.ForbiddenException;
import com.mediscript.backend.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            UserRepository userRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    public Appointment createAppointment(
            String patientEmail,
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            String reason
    ) {

        User user = userRepository.findByEmail(patientEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        Patient patient = patientRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient profile not found")
                );

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Doctor not found")
                );

        if (appointmentRepository
                .existsByDoctorAndAppointmentDateAndAppointmentTime(
                        doctor,
                        appointmentDate,
                        appointmentTime
                )) {

            throw new BadRequestException(
                    "Doctor is already booked for this time"
            );
        }

        Appointment appointment = new Appointment(
                patient,
                doctor,
                appointmentDate,
                appointmentTime,
                reason,
                "PENDING",
                null
        );

        return appointmentRepository.save(appointment);
    }

    /**
     * Creates a walk-in appointment for right now (today, current time to
     * the minute) with status CONFIRMED. Used by the walk-in registration flow,
     * where the patient and doctor have already been resolved.
     */
    public Appointment createWalkInAppointment(
            Patient patient,
            Doctor doctor,
            String reason
    ) {

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);

        if (appointmentRepository
                .existsByDoctorAndAppointmentDateAndAppointmentTime(
                        doctor,
                        today,
                        now
                )) {

            throw new ConflictException(
                    "Doctor already has an appointment at this minute. "
                            + "Please try again in a moment"
            );
        }

        Appointment appointment = new Appointment(
                patient,
                doctor,
                today,
                now,
                reason,
                "CONFIRMED",
                null
        );

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getPatientAppointments(
            String patientEmail
    ) {

        User user = userRepository.findByEmail(patientEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        Patient patient = patientRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient profile not found")
                );

        return appointmentRepository.findByPatient(patient);
    }

    public List<Appointment> getDoctorAppointments(
            String doctorEmail
    ) {

        User user = userRepository.findByEmail(doctorEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Doctor profile not found")
                );

        return appointmentRepository.findByDoctor(doctor);
    }

    public Appointment updateAppointmentStatus(
            String doctorEmail,
            Long appointmentId,
            String newStatus
    ) {

        User user = userRepository.findByEmail(doctorEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Doctor profile not found")
                );

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Appointment not found")
                );

        // Make sure this appointment belongs to the logged-in doctor
        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new ForbiddenException(
                    "You are not authorized to modify this appointment"
            );
        }

        String currentStatus = appointment.getStatus();

        // Validate allowed status transitions
        if ("PENDING".equals(currentStatus)) {

            if (!"CONFIRMED".equals(newStatus)
                    && !"CANCELLED".equals(newStatus)) {

                throw new BadRequestException(
                        "Invalid status transition"
                );
            }

        } else if ("CONFIRMED".equals(currentStatus)) {

            if (!"COMPLETED".equals(newStatus)
                    && !"CANCELLED".equals(newStatus)) {

                throw new BadRequestException(
                        "Invalid status transition"
                );
            }

        } else {

            throw new BadRequestException(
                    "Appointment can no longer be modified"
            );
        }

        appointment.setStatus(newStatus);

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long appointmentId) {

        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Appointment not found")
                );
    }
}
