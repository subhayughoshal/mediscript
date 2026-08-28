package com.mediscript.backend.audit.service;

import com.mediscript.backend.audit.dto.AuditLogResponse;
import java.util.List;
import com.mediscript.backend.audit.entity.AuditLog;
import com.mediscript.backend.audit.repository.AuditLogRepository;
import com.mediscript.backend.exception.ResourceNotFoundException;
import com.mediscript.backend.user.entity.User;
import com.mediscript.backend.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditLogService(
            AuditLogRepository auditLogRepository,
            UserRepository userRepository
    ) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    public void log(
            String action,
            String resourceType,
            Long resourceId,
            String details
    ) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null) {

            return;
        }

        String actorEmail = authentication.getName();

        User actor = userRepository.findByEmail(actorEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Authenticated user not found")
                );

        AuditLog auditLog = new AuditLog(
                actor.getId(),
                actor.getEmail(),
                action,
                resourceType,
                resourceId,
                details
        );

        auditLogRepository.save(auditLog);
    }

    public List<AuditLogResponse> getAllLogs() {

        return auditLogRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(log ->
                        new AuditLogResponse(
                                log.getId(),
                                log.getActorUserId(),
                                log.getActorEmail(),
                                log.getAction(),
                                log.getResourceType(),
                                log.getResourceId(),
                                log.getDetails(),
                                log.getCreatedAt()
                        )
                )
                .toList();
    }

    public AuditLogResponse getLogById(Long id) {

        AuditLog log = auditLogRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Audit log not found")
                );

        return new AuditLogResponse(
                log.getId(),
                log.getActorUserId(),
                log.getActorEmail(),
                log.getAction(),
                log.getResourceType(),
                log.getResourceId(),
                log.getDetails(),
                log.getCreatedAt()
        );
    }
}