package com.mediscript.backend.admin.controller;

import com.mediscript.backend.audit.dto.AuditLogResponse;
import com.mediscript.backend.audit.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-logs")
public class AdminAuditLogController {

    private final AuditLogService auditLogService;

    public AdminAuditLogController(
            AuditLogService auditLogService
    ) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAllLogs() {

        return ResponseEntity.ok(
                auditLogService.getAllLogs()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLogResponse> getLogById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                auditLogService.getLogById(id)
        );
    }
}