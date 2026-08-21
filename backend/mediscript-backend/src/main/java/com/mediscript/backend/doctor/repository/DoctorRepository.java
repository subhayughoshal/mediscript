package com.mediscript.backend.doctor.repository;

import com.mediscript.backend.doctor.entity.Doctor;
import com.mediscript.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByUser(User user);

    boolean existsByUser(User user);

    boolean existsByLicenseNumber(String licenseNumber);
}