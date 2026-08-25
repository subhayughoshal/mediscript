package com.mediscript.backend.admin.service;

import com.mediscript.backend.admin.dto.AdminDashboardResponse;
import com.mediscript.backend.appointment.repository.AppointmentRepository;
import com.mediscript.backend.doctor.repository.DoctorRepository;
import com.mediscript.backend.patient.repository.PatientRepository;
import com.mediscript.backend.prescription.repository.PrescriptionRepository;
import com.mediscript.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;

    public AdminDashboardService(
            UserRepository userRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            PrescriptionRepository prescriptionRepository
    ) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    public AdminDashboardResponse getDashboard() {

        long totalUsers = userRepository.count();
        long totalDoctors = doctorRepository.count();
        long totalPatients = patientRepository.count();
        long totalAppointments = appointmentRepository.count();

        long pendingAppointments =
                appointmentRepository.countByStatus("PENDING");

        long confirmedAppointments =
                appointmentRepository.countByStatus("CONFIRMED");

        long completedAppointments =
                appointmentRepository.countByStatus("COMPLETED");

        long cancelledAppointments =
                appointmentRepository.countByStatus("CANCELLED");

        long totalPrescriptions = prescriptionRepository.count();

        return new AdminDashboardResponse(
                totalUsers,
                totalDoctors,
                totalPatients,
                totalAppointments,
                pendingAppointments,
                confirmedAppointments,
                completedAppointments,
                cancelledAppointments,
                totalPrescriptions
        );
    }
}