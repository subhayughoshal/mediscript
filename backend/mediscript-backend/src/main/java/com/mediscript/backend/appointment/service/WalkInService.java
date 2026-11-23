package com.mediscript.backend.appointment.service;

import com.mediscript.backend.appointment.dto.WalkInRequest;
import com.mediscript.backend.appointment.entity.Appointment;
import com.mediscript.backend.doctor.entity.Doctor;
import com.mediscript.backend.doctor.repository.DoctorRepository;
import com.mediscript.backend.exception.BadRequestException;
import com.mediscript.backend.exception.ResourceNotFoundException;
import com.mediscript.backend.patient.entity.Patient;
import com.mediscript.backend.patient.repository.PatientRepository;
import com.mediscript.backend.patient.service.PatientService;
import com.mediscript.backend.user.entity.User;
import com.mediscript.backend.user.repository.UserRepository;
import com.mediscript.backend.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Registers a walk-in patient (account and profile, only if they do not
 * exist yet) and books a CONFIRMED appointment for right now.
 *
 * The appointment is what links the patient to the doctor, so everything
 * that checks "does this doctor have an appointment with this patient"
 * works for walk-ins without any extra table.
 *
 * Everything runs in one transaction: if booking fails (for example the
 * doctor already has an appointment at this minute), the new user and
 * profile are rolled back too.
 */
@Service
public class WalkInService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PatientRepository patientRepository;
    private final PatientService patientService;
    private final DoctorRepository doctorRepository;
    private final AppointmentService appointmentService;

    private final SecureRandom secureRandom = new SecureRandom();

    public WalkInService(
            UserRepository userRepository,
            UserService userService,
            PatientRepository patientRepository,
            PatientService patientService,
            DoctorRepository doctorRepository,
            AppointmentService appointmentService
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.patientRepository = patientRepository;
        this.patientService = patientService;
        this.doctorRepository = doctorRepository;
        this.appointmentService = appointmentService;
    }

    public record WalkInResult(
            Appointment appointment,
            boolean newAccountCreated,
            boolean newProfileCreated
    ) {
    }

    // Receptionist / admin path: the doctor is chosen by id
    @Transactional
    public WalkInResult registerWalkInForDoctorId(
            WalkInRequest request,
            Long doctorId
    ) {

        if (doctorId == null) {
            throw new BadRequestException("Doctor ID is required");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Doctor not found")
                );

        return process(request, doctor);
    }

    // Doctor path: the doctor is the logged-in doctor
    @Transactional
    public WalkInResult registerWalkInForDoctorEmail(
            WalkInRequest request,
            String doctorEmail
    ) {

        User doctorUser = userRepository.findByEmail(doctorEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        Doctor doctor = doctorRepository.findByUser(doctorUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Doctor profile not found")
                );

        return process(request, doctor);
    }

    private WalkInResult process(WalkInRequest request, Doctor doctor) {

        if (!"ACTIVE".equalsIgnoreCase(doctor.getUser().getStatus())) {
            throw new BadRequestException("Doctor is not available");
        }

        String email = request.getEmail().trim();

        User user = userRepository.findByEmail(email).orElse(null);
        Patient patient = null;

        if (user != null) {

            if (!"PATIENT".equalsIgnoreCase(user.getRole().getName())) {
                throw new BadRequestException(
                        "This email belongs to a non-patient account"
                );
            }

            if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
                throw new BadRequestException(
                        "This patient account is inactive"
                );
            }

            patient = patientRepository.findByUser(user).orElse(null);
        }

        // New account and/or missing profile: the details must be supplied
        if (patient == null) {
            requireNewPatientDetails(request);
        }

        boolean newAccountCreated = false;
        boolean newProfileCreated = false;

        if (user == null) {

            user = userService.createUser(
                    request.getName().trim(),
                    email,
                    generateUnusablePassword(),
                    request.getPhone().trim(),
                    "PATIENT"
            );

            newAccountCreated = true;
        }

        if (patient == null) {

            patient = patientService.createPatient(
                    user.getId(),
                    request.getDateOfBirth(),
                    request.getGender().trim(),
                    blankToNull(request.getBloodGroup()),
                    blankToNull(request.getAddress()),
                    blankToNull(request.getEmergencyContact())
            );

            newProfileCreated = true;
        }

        Appointment appointment = appointmentService.createWalkInAppointment(
                patient,
                doctor,
                request.getReason().trim()
        );

        return new WalkInResult(
                appointment,
                newAccountCreated,
                newProfileCreated
        );
    }

    private void requireNewPatientDetails(WalkInRequest request) {

        List<String> missing = new ArrayList<>();

        if (isBlank(request.getName())) {
            missing.add("name");
        }

        if (isBlank(request.getPhone())) {
            missing.add("phone");
        }

        if (request.getDateOfBirth() == null) {
            missing.add("dateOfBirth");
        }

        if (isBlank(request.getGender())) {
            missing.add("gender");
        }

        if (!missing.isEmpty()) {
            throw new BadRequestException(
                    "A new patient requires: " + String.join(", ", missing)
            );
        }
    }

    /**
     * A long random password that nobody ever sees. The account cannot be
     * logged into until a set-password flow exists.
     * (48 random bytes = 64 characters, within BCrypt's 72-byte limit.)
     */
    private String generateUnusablePassword() {

        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }
}
