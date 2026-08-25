package com.mediscript.backend.admin.controller;

import com.mediscript.backend.admin.dto.AdminDashboardResponse;
import com.mediscript.backend.admin.service.AdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(
            AdminDashboardService adminDashboardService
    ) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping
    public ResponseEntity<AdminDashboardResponse> getDashboard() {

        AdminDashboardResponse response =
                adminDashboardService.getDashboard();

        return ResponseEntity.ok(response);
    }
}