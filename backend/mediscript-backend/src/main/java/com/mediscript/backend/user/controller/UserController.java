package com.mediscript.backend.user.controller;

import com.mediscript.backend.user.dto.CreateUserRequest;
import com.mediscript.backend.user.entity.User;
import com.mediscript.backend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(
            @RequestBody CreateUserRequest request
    ) {

        User user = userService.createUser(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getPhone(),
                request.getRole()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }
}