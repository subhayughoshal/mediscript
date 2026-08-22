package com.mediscript.backend.prescription.repository;

import com.mediscript.backend.appointment.entity.Appointment;
import com.mediscript.backend.doctor.entity.Doctor;
import com.mediscript.backend.patient.entity.Patient;
import com.mediscript.backend.prescription.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository
        extends JpaRepository<Prescription, Long> {

    List<Prescription> findByPatient(Patient patient);

    List<Prescription> findByDoctor(Doctor doctor);

    // An appointment can have at most one prescription in our design.
    Optional<Prescription> findByAppointment(Appointment appointment);

    boolean existsByAppointment(Appointment appointment);
}