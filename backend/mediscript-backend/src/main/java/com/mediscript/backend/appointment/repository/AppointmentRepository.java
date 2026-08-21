package com.mediscript.backend.appointment.repository;

import com.mediscript.backend.appointment.entity.Appointment;
import com.mediscript.backend.doctor.entity.Doctor;
import com.mediscript.backend.patient.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatient(Patient patient);

    List<Appointment> findByDoctor(Doctor doctor);

    boolean existsByDoctorAndAppointmentDateAndAppointmentTime(
            Doctor doctor,
            LocalDate appointmentDate,
            java.time.LocalTime appointmentTime
    );
}