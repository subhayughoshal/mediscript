package com.mediscript.backend.patient.service;

import com.mediscript.backend.appointment.repository.AppointmentRepository;
import com.mediscript.backend.doctor.entity.Doctor;
import com.mediscript.backend.doctor.repository.DoctorRepository;
import com.mediscript.backend.exception.BadRequestException;
import com.mediscript.backend.exception.ForbiddenException;
import com.mediscript.backend.exception.ResourceNotFoundException;
import com.mediscript.backend.patient.entity.Patient;
import com.mediscript.backend.patient.repository.PatientRepository;
import com.mediscript.backend.user.entity.User;
import com.mediscript.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public PatientService(
            PatientRepository patientRepository,
            UserRepository userRepository,
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository
    ) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Creates a patient profile for the given user, without any caller check.
     * Used internally (for example by the walk-in flow) and by the
     * caller-checked overload below.
     */
    public Patient createPatient(
            Long userId,
            LocalDate dateOfBirth,
            String gender,
            String bloodGroup,
            String address,
            String emergencyContact
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        if (!"PATIENT".equalsIgnoreCase(user.getRole().getName())) {
            throw new BadRequestException(
                    "Patient profile can only be created for PATIENT users"
            );
        }

        if (patientRepository.existsByUser(user)) {
            throw new BadRequestException(
                    "Patient profile already exists"
            );
        }

        Patient patient = new Patient(
                user,
                dateOfBirth,
                gender,
                bloodGroup,
                address,
                emergencyContact
        );

        return patientRepository.save(patient);
    }

    /**
     * Caller-checked version used by POST /api/patients:
     * an ADMIN may create a profile for any PATIENT user,
     * anyone else may only create a profile for their own user account.
     */
    public Patient createPatient(
            String callerEmail,
            boolean isAdmin,
            Long userId,
            LocalDate dateOfBirth,
            String gender,
            String bloodGroup,
            String address,
            String emergencyContact
    ) {

        if (!isAdmin) {

            User caller = userRepository.findByEmail(callerEmail)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("User not found")
                    );

            if (!caller.getId().equals(userId)) {
                throw new ForbiddenException(
                        "You can only create your own patient profile"
                );
            }
        }

        return createPatient(
                userId,
                dateOfBirth,
                gender,
                bloodGroup,
                address,
                emergencyContact
        );
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long patientId) {

        return patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient not found")
                );
    }

    // ---------- Patient: own profile ----------

    public Patient getPatientByEmail(String patientEmail) {

        User user = userRepository.findByEmail(patientEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        return patientRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient profile not found")
                );
    }

    public Patient updateMyProfile(
            String patientEmail,
            LocalDate dateOfBirth,
            String gender,
            String bloodGroup,
            String address,
            String emergencyContact
    ) {

        Patient patient = getPatientByEmail(patientEmail);

        return applyUpdate(
                patient,
                dateOfBirth,
                gender,
                bloodGroup,
                address,
                emergencyContact
        );
    }

    // ---------- Doctor: patients they have an appointment with ----------

    public Patient getPatientForDoctor(String doctorEmail, Long patientId) {

        Doctor doctor = getDoctorByEmail(doctorEmail);

        Patient patient = getPatientById(patientId);

        // A doctor may only access patients they have a
        // non-cancelled appointment with
        if (!appointmentRepository.existsByDoctorAndPatientAndStatusNot(
                doctor,
                patient,
                "CANCELLED"
        )) {
            throw new ForbiddenException(
                    "You do not have an appointment with this patient"
            );
        }

        return patient;
    }

    public Patient updatePatientForDoctor(
            String doctorEmail,
            Long patientId,
            LocalDate dateOfBirth,
            String gender,
            String bloodGroup,
            String address,
            String emergencyContact
    ) {

        Patient patient = getPatientForDoctor(doctorEmail, patientId);

        return applyUpdate(
                patient,
                dateOfBirth,
                gender,
                bloodGroup,
                address,
                emergencyContact
        );
    }

    // ---------- helpers ----------

    private Doctor getDoctorByEmail(String doctorEmail) {

        User user = userRepository.findByEmail(doctorEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        return doctorRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Doctor profile not found")
                );
    }

    private Patient applyUpdate(
            Patient patient,
            LocalDate dateOfBirth,
            String gender,
            String bloodGroup,
            String address,
            String emergencyContact
    ) {

        patient.setDateOfBirth(dateOfBirth);
        patient.setGender(gender);
        patient.setBloodGroup(bloodGroup);
        patient.setAddress(address);
        patient.setEmergencyContact(emergencyContact);

        return patientRepository.save(patient);
    }
}
