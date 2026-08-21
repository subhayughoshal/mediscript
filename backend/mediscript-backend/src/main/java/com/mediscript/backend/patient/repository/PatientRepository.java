package com.mediscript.backend.patient.repository;

import com.mediscript.backend.patient.entity.Patient;
import com.mediscript.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUser(User user);

    boolean existsByUser(User user);
}