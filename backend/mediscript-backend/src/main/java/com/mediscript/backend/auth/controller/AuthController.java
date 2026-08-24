package com.mediscript.backend.auth.controller;

import com.mediscript.backend.auth.dto.LoginRequest;
import com.mediscript.backend.auth.dto.LoginResponse;
import com.mediscript.backend.auth.service.AuthenticationService;
import com.mediscript.backend.auth.service.JwtService;
import com.mediscript.backend.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationService authenticationService,
            JwtService jwtService
    ) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        User user = authenticationService.authenticate(
                request.getEmail(),
                request.getPassword()
        );

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getName()
        );

        LoginResponse response = new LoginResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().getName()
        );

        return ResponseEntity.ok(response);
    }
}