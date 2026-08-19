package com.mediscript.backend.auth.controller;

import com.mediscript.backend.auth.dto.LoginRequest;
import com.mediscript.backend.auth.service.AuthenticationService;
import com.mediscript.backend.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequest request
    ) {

        User user = authenticationService.authenticate(
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(
                "Login successful for " + user.getEmail()
        );
    }
}