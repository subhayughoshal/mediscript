package com.mediscript.backend.admin.dto;

public class AdminDashboardResponse {

    private long totalUsers;
    private long totalDoctors;
    private long totalPatients;
    private long totalAppointments;

    private long pendingAppointments;
    private long confirmedAppointments;
    private long completedAppointments;
    private long cancelledAppointments;

    private long totalPrescriptions;

    public AdminDashboardResponse(
            long totalUsers,
            long totalDoctors,
            long totalPatients,
            long totalAppointments,
            long pendingAppointments,
            long confirmedAppointments,
            long completedAppointments,
            long cancelledAppointments,
            long totalPrescriptions
    ) {
        this.totalUsers = totalUsers;
        this.totalDoctors = totalDoctors;
        this.totalPatients = totalPatients;
        this.totalAppointments = totalAppointments;
        this.pendingAppointments = pendingAppointments;
        this.confirmedAppointments = confirmedAppointments;
        this.completedAppointments = completedAppointments;
        this.cancelledAppointments = cancelledAppointments;
        this.totalPrescriptions = totalPrescriptions;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalDoctors() {
        return totalDoctors;
    }

    public long getTotalPatients() {
        return totalPatients;
    }

    public long getTotalAppointments() {
        return totalAppointments;
    }

    public long getPendingAppointments() {
        return pendingAppointments;
    }

    public long getConfirmedAppointments() {
        return confirmedAppointments;
    }

    public long getCompletedAppointments() {
        return completedAppointments;
    }

    public long getCancelledAppointments() {
        return cancelledAppointments;
    }

    public long getTotalPrescriptions() {
        return totalPrescriptions;
    }
}