package com.mediscript.backend.patient.service;

import com.mediscript.backend.appointment.repository.AppointmentRepository;
import com.mediscript.backend.doctor.entity.Doctor;
import com.mediscript.backend.doctor.repository.DoctorRepository;
import com.mediscript.backend.exception.ForbiddenException;
import com.mediscript.backend.patient.entity.Patient;
import com.mediscript.backend.patient.repository.PatientRepository;
import com.mediscript.backend.user.entity.Role;
import com.mediscript.backend.user.entity.User;
import com.mediscript.backend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private PatientService patientService;

    // ---------- creating a profile ----------

    @Test
    void patientCannotCreateAProfileForAnotherUser() {

        User caller = mock(User.class);
        when(caller.getId()).thenReturn(1L);
        when(userRepository.findByEmail("me@test.com"))
                .thenReturn(Optional.of(caller));

        assertThrows(
                ForbiddenException.class,
                () -> patientService.createPatient(
                        "me@test.com", false, 2L,
                        LocalDate.of(1990, 1, 1), "Male", "O+", "Addr", "999"
                )
        );
    }

    @Test
    void patientCanCreateTheirOwnProfile() {

        User caller = mock(User.class);
        when(caller.getId()).thenReturn(1L);
        when(caller.getRole()).thenReturn(new Role("PATIENT"));
        when(userRepository.findByEmail("me@test.com"))
                .thenReturn(Optional.of(caller));
        when(userRepository.findById(1L)).thenReturn(Optional.of(caller));
        when(patientRepository.existsByUser(caller)).thenReturn(false);
        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Patient created = patientService.createPatient(
                "me@test.com", false, 1L,
                LocalDate.of(1990, 1, 1), "Male", "O+", "Addr", "999"
        );

        assertEquals("Male", created.getGender());
    }

    @Test
    void adminCanCreateAProfileForAnyPatientUser() {

        User target = mock(User.class);
        when(target.getRole()).thenReturn(new Role("PATIENT"));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(patientRepository.existsByUser(target)).thenReturn(false);
        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Patient created = patientService.createPatient(
                "admin@test.com", true, 2L,
                LocalDate.of(1990, 1, 1), "Female", "A+", "Addr", "999"
        );

        assertEquals("Female", created.getGender());
    }

    // ---------- doctor access ----------

    private Doctor loggedInDoctor() {

        User doctorUser = new User(
                "Dr Test", "dr@test.com", "hash", "1111111111",
                new Role("DOCTOR"), "ACTIVE"
        );
        Doctor doctor = mock(Doctor.class);

        when(userRepository.findByEmail("dr@test.com"))
                .thenReturn(Optional.of(doctorUser));
        when(doctorRepository.findByUser(doctorUser))
                .thenReturn(Optional.of(doctor));

        return doctor;
    }

    @Test
    void doctorWithoutAnAppointmentCannotViewAPatient() {

        Doctor doctor = loggedInDoctor();
        Patient patient = mock(Patient.class);

        when(patientRepository.findById(5L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.existsByDoctorAndPatientAndStatusNot(
                doctor, patient, "CANCELLED"
        )).thenReturn(false);

        assertThrows(
                ForbiddenException.class,
                () -> patientService.getPatientForDoctor("dr@test.com", 5L)
        );
    }

    @Test
    void doctorWithAnAppointmentCanViewAPatient() {

        Doctor doctor = loggedInDoctor();
        Patient patient = mock(Patient.class);

        when(patientRepository.findById(5L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.existsByDoctorAndPatientAndStatusNot(
                doctor, patient, "CANCELLED"
        )).thenReturn(true);

        assertEquals(
                patient,
                patientService.getPatientForDoctor("dr@test.com", 5L)
        );
    }
}
