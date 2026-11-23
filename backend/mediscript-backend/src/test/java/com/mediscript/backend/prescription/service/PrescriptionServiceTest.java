package com.mediscript.backend.prescription.service;

import com.mediscript.backend.appointment.entity.Appointment;
import com.mediscript.backend.appointment.repository.AppointmentRepository;
import com.mediscript.backend.doctor.entity.Doctor;
import com.mediscript.backend.doctor.repository.DoctorRepository;
import com.mediscript.backend.exception.BadRequestException;
import com.mediscript.backend.exception.ForbiddenException;
import com.mediscript.backend.patient.entity.Patient;
import com.mediscript.backend.patient.repository.PatientRepository;
import com.mediscript.backend.prescription.dto.CreatePrescriptionRequest;
import com.mediscript.backend.prescription.dto.PrescriptionItemRequest;
import com.mediscript.backend.prescription.entity.Prescription;
import com.mediscript.backend.prescription.repository.PrescriptionRepository;
import com.mediscript.backend.user.entity.Role;
import com.mediscript.backend.user.entity.User;
import com.mediscript.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PrescriptionServiceTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PrescriptionService prescriptionService;

    private Doctor doctor;
    private Patient patient;

    @BeforeEach
    void setUp() {

        User doctorUser = new User(
                "Dr Test", "dr@test.com", "hash", "1111111111",
                new Role("DOCTOR"), "ACTIVE"
        );

        doctor = mock(Doctor.class);
        patient = mock(Patient.class);

        when(doctor.getId()).thenReturn(1L);
        when(patient.getId()).thenReturn(5L);

        when(userRepository.findByEmail("dr@test.com"))
                .thenReturn(Optional.of(doctorUser));
        when(doctorRepository.findByUser(doctorUser))
                .thenReturn(Optional.of(doctor));
        when(patientRepository.findById(5L))
                .thenReturn(Optional.of(patient));
    }

    private CreatePrescriptionRequest requestWithOneMedicine(
            Long appointmentId
    ) {

        PrescriptionItemRequest item = new PrescriptionItemRequest();
        item.setMedicineName("Paracetamol");
        item.setDosage("500mg");
        item.setFrequency("Twice a day");
        item.setDuration("5 days");

        CreatePrescriptionRequest request = new CreatePrescriptionRequest();
        request.setPatientId(5L);
        request.setAppointmentId(appointmentId);
        request.setPrescriptionDate(LocalDate.now());
        request.setDiagnosis("Fever");
        request.setItems(List.of(item));

        return request;
    }

    @Test
    void cannotPrescribeWithoutAnyAppointmentWithThePatient() {

        when(appointmentRepository.existsByDoctorAndPatientAndStatusNot(
                doctor, patient, "CANCELLED"
        )).thenReturn(false);

        assertThrows(
                ForbiddenException.class,
                () -> prescriptionService.createPrescription(
                        "dr@test.com", requestWithOneMedicine(null)
                )
        );

        verify(prescriptionRepository, never()).save(any());
    }

    @Test
    void canPrescribeWithoutAppointmentIdWhenARelationshipExists() {

        when(appointmentRepository.existsByDoctorAndPatientAndStatusNot(
                doctor, patient, "CANCELLED"
        )).thenReturn(true);
        when(prescriptionRepository.save(any(Prescription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Prescription saved = prescriptionService.createPrescription(
                "dr@test.com", requestWithOneMedicine(null)
        );

        assertNotNull(saved);
        verify(prescriptionRepository).save(any(Prescription.class));
    }

    @Test
    void cannotPrescribeAgainstACancelledAppointment() {

        Appointment cancelled = mock(Appointment.class);
        when(cancelled.getDoctor()).thenReturn(doctor);
        when(cancelled.getPatient()).thenReturn(patient);
        when(cancelled.getStatus()).thenReturn("CANCELLED");
        when(appointmentRepository.findById(9L))
                .thenReturn(Optional.of(cancelled));

        assertThrows(
                BadRequestException.class,
                () -> prescriptionService.createPrescription(
                        "dr@test.com", requestWithOneMedicine(9L)
                )
        );

        verify(prescriptionRepository, never()).save(any());
    }
}
