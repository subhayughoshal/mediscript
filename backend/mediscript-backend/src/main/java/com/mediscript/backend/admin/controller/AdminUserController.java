package com.mediscript.backend.admin.controller;

import com.mediscript.backend.user.dto.CreateUserRequest;
import com.mediscript.backend.user.dto.UserResponse;
import com.mediscript.backend.user.entity.User;
import com.mediscript.backend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody CreateUserRequest request
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
}