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

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
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