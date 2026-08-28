package com.mediscript.backend.admin.controller;

import java.util.List;
import com.mediscript.backend.user.dto.CreateUserRequest;
import com.mediscript.backend.user.dto.UserResponse;
import com.mediscript.backend.user.entity.User;
import com.mediscript.backend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mediscript.backend.admin.dto.UpdateUserStatusRequest;
import jakarta.validation.Valid;
import com.mediscript.backend.admin.dto.UpdateUserRequest;
import com.mediscript.backend.audit.service.AuditLogService;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final AuditLogService auditLogService;

    public AdminUserController(UserService userService, AuditLogService auditLogService) {
        this.userService = userService;
        this.auditLogService = auditLogService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {

        User user = userService.createUser(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getPhone(),
                request.getRole()
        );

        auditLogService.log(
                "USER_CREATED",
                "USER",
                user.getId(),
                "Created user with role " + user.getRole().getName()
        );

        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().getName(),
                user.getStatus()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> response =
                userService.getAllUsers()
                        .stream()
                        .map(user -> new UserResponse(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getPhone(),
                                user.getRole().getName(),
                                user.getStatus()
                        ))
                        .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request
    ) {

        User user = userService.updateUserStatus(
                id,
                request.getStatus()
        );

        auditLogService.log(
                "USER_STATUS_CHANGED",
                "USER",
                user.getId(),
                "Changed user status to " + user.getStatus()
        );

        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().getName(),
                user.getStatus()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id
    ) {

        User user = userService.getUserById(id);

        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().getName(),
                user.getStatus()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {

        User user = userService.updateUser(
                id,
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getRole()
        );

        auditLogService.log(
                "USER_UPDATED",
                "USER",
                user.getId(),
                "Updated user details"
        );

        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().getName(),
                user.getStatus()
        );

        return ResponseEntity.ok(response);
    }
}