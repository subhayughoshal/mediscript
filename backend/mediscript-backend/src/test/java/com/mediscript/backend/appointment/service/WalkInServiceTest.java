package com.mediscript.backend.appointment.service;

import com.mediscript.backend.appointment.dto.WalkInRequest;
import com.mediscript.backend.appointment.entity.Appointment;
import com.mediscript.backend.doctor.entity.Doctor;
import com.mediscript.backend.doctor.repository.DoctorRepository;
import com.mediscript.backend.exception.BadRequestException;
import com.mediscript.backend.patient.entity.Patient;
import com.mediscript.backend.patient.repository.PatientRepository;
import com.mediscript.backend.patient.service.PatientService;
import com.mediscript.backend.user.entity.Role;
import com.mediscript.backend.user.entity.User;
import com.mediscript.backend.user.repository.UserRepository;
import com.mediscript.backend.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WalkInServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientService patientService;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private WalkInService walkInService;

    // ---------- helpers ----------

    private Doctor doctorWithStatus(String status) {

        User doctorUser = new User(
                "Dr Test", "dr@test.com", "hash", "1111111111",
                new Role("DOCTOR"), status
        );

        Doctor doctor = mock(Doctor.class);
        when(doctor.getId()).thenReturn(7L);
        when(doctor.getUser()).thenReturn(doctorUser);
        when(doctorRepository.findById(7L)).thenReturn(Optional.of(doctor));

        return doctor;
    }

    private WalkInRequest fullRequest(String email) {

        WalkInRequest request = new WalkInRequest();
        request.setEmail(email);
        request.setName("New Patient");
        request.setPhone("9876543210");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setReason("Fever");

        return request;
    }

    private WalkInRequest emailAndReasonOnly(String email) {

        WalkInRequest request = new WalkInRequest();
        request.setEmail(email);
        request.setReason("Checkup");

        return request;
    }

    // ---------- tests ----------

    @Test
    void newPatientGetsAccountProfileAndAppointment() {

        Doctor doctor = doctorWithStatus("ACTIVE");

        User created = new User(
                "New Patient", "new@test.com", "hash", "9876543210",
                new Role("PATIENT"), "ACTIVE"
        );
        Patient patient = new Patient(
                created, LocalDate.of(1990, 1, 1), "Male", null, null, null
        );
        Appointment appointment = new Appointment(
                patient, doctor, LocalDate.now(), LocalTime.now(),
                "Fever", "CONFIRMED", null
        );

        when(userRepository.findByEmail("new@test.com"))
                .thenReturn(Optional.empty());
        when(userService.createUser(
                anyString(), anyString(), anyString(), anyString(), eq("PATIENT")
        )).thenReturn(created);
        when(patientService.createPatient(
                any(), any(), any(), any(), any(), any()
        )).thenReturn(patient);
        when(appointmentService.createWalkInAppointment(
                any(), any(), anyString()
        )).thenReturn(appointment);

        WalkInService.WalkInResult result =
                walkInService.registerWalkInForDoctorId(
                        fullRequest("new@test.com"), 7L
                );

        assertTrue(result.newAccountCreated());
        assertTrue(result.newProfileCreated());
        assertEquals("CONFIRMED", result.appointment().getStatus());

        // The account is created with a long random password, never a
        // predictable one
        ArgumentCaptor<String> password = ArgumentCaptor.forClass(String.class);
        verify(userService).createUser(
                eq("New Patient"),
                eq("new@test.com"),
                password.capture(),
                eq("9876543210"),
                eq("PATIENT")
        );
        assertTrue(password.getValue().length() >= 32);
    }

    @Test
    void existingPatientIsReusedWithoutCreatingAnything() {

        Doctor doctor = doctorWithStatus("ACTIVE");

        User existing = new User(
                "Old Patient", "old@test.com", "hash", "2222222222",
                new Role("PATIENT"), "ACTIVE"
        );
        Patient patient = new Patient(
                existing, LocalDate.of(1985, 5, 5), "Female", null, null, null
        );
        Appointment appointment = new Appointment(
                patient, doctor, LocalDate.now(), LocalTime.now(),
                "Checkup", "CONFIRMED", null
        );

        when(userRepository.findByEmail("old@test.com"))
                .thenReturn(Optional.of(existing));
        when(patientRepository.findByUser(existing))
                .thenReturn(Optional.of(patient));
        when(appointmentService.createWalkInAppointment(
                patient, doctor, "Checkup"
        )).thenReturn(appointment);

        // Only email and reason: enough for a patient who is already known
        WalkInService.WalkInResult result =
                walkInService.registerWalkInForDoctorId(
                        emailAndReasonOnly("old@test.com"), 7L
                );

        assertFalse(result.newAccountCreated());
        assertFalse(result.newProfileCreated());

        verify(userService, never()).createUser(
                any(), any(), any(), any(), any()
        );
        verify(patientService, never()).createPatient(
                any(), any(), any(), any(), any(), any()
        );
    }

    @Test
    void existingAccountWithoutProfileGetsOnlyAProfile() {

        Doctor doctor = doctorWithStatus("ACTIVE");

        User existing = new User(
                "Half Registered", "half@test.com", "hash", "3333333333",
                new Role("PATIENT"), "ACTIVE"
        );
        Patient patient = new Patient(
                existing, LocalDate.of(2000, 2, 2), "Male", null, null, null
        );
        Appointment appointment = new Appointment(
                patient, doctor, LocalDate.now(), LocalTime.now(),
                "Fever", "CONFIRMED", null
        );

        when(userRepository.findByEmail("half@test.com"))
                .thenReturn(Optional.of(existing));
        when(patientRepository.findByUser(existing))
                .thenReturn(Optional.empty());
        when(patientService.createPatient(
                any(), any(), any(), any(), any(), any()
        )).thenReturn(patient);
        when(appointmentService.createWalkInAppointment(
                any(), any(), anyString()
        )).thenReturn(appointment);

        WalkInService.WalkInResult result =
                walkInService.registerWalkInForDoctorId(
                        fullRequest("half@test.com"), 7L
                );

        assertFalse(result.newAccountCreated());
        assertTrue(result.newProfileCreated());

        verify(userService, never()).createUser(
                any(), any(), any(), any(), any()
        );
    }

    @Test
    void emailOfANonPatientAccountIsRejected() {

        doctorWithStatus("ACTIVE");

        User otherRole = new User(
                "Some Doctor", "other@test.com", "hash", "4444444444",
                new Role("DOCTOR"), "ACTIVE"
        );
        when(userRepository.findByEmail("other@test.com"))
                .thenReturn(Optional.of(otherRole));

        assertThrows(
                BadRequestException.class,
                () -> walkInService.registerWalkInForDoctorId(
                        fullRequest("other@test.com"), 7L
                )
        );
    }

    @Test
    void newPatientWithMissingDetailsIsRejectedAndNothingIsCreated() {

        doctorWithStatus("ACTIVE");

        when(userRepository.findByEmail("new@test.com"))
                .thenReturn(Optional.empty());

        WalkInRequest request = emailAndReasonOnly("new@test.com");

        assertThrows(
                BadRequestException.class,
                () -> walkInService.registerWalkInForDoctorId(request, 7L)
        );

        verify(userService, never()).createUser(
                any(), any(), any(), any(), any()
        );
    }

    @Test
    void inactiveDoctorIsRejected() {

        doctorWithStatus("INACTIVE");

        assertThrows(
                BadRequestException.class,
                () -> walkInService.registerWalkInForDoctorId(
                        fullRequest("new@test.com"), 7L
                )
        );
    }

    @Test
    void receptionPathRequiresADoctorId() {

        assertThrows(
                BadRequestException.class,
                () -> walkInService.registerWalkInForDoctorId(
                        fullRequest("new@test.com"), null
                )
        );
    }
}
