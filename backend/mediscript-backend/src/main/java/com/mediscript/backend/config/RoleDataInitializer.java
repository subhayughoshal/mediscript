package com.mediscript.backend.config;

import com.mediscript.backend.user.entity.Role;
import com.mediscript.backend.user.entity.User;
import com.mediscript.backend.user.repository.RoleRepository;
import com.mediscript.backend.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class RoleDataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RoleDataInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        createRoleIfNotExists("ADMIN");
        createRoleIfNotExists("DOCTOR");
        createRoleIfNotExists("PATIENT");
        createRoleIfNotExists("RECEPTIONIST");

        createAdminIfNotExists();
    }

    private void createRoleIfNotExists(String roleName) {

        roleRepository.findByName(roleName)
                .orElseGet(() ->
                        roleRepository.save(new Role(roleName))
                );
    }

    private void createAdminIfNotExists() {

        String adminEmail = System.getenv("ADMIN_EMAIL");
        String adminPassword = System.getenv("ADMIN_PASSWORD");

        if (adminEmail == null || adminEmail.isBlank()) {
            throw new RuntimeException(
                    "ADMIN_EMAIL environment variable is not configured"
            );
        }

        if (adminPassword == null || adminPassword.isBlank()) {
            throw new RuntimeException(
                    "ADMIN_PASSWORD environment variable is not configured"
            );
        }

        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() ->
                        new RuntimeException("ADMIN role not found")
                );

        User admin = new User(
                "System Administrator",
                adminEmail,
                passwordEncoder.encode(adminPassword),
                null,
                adminRole,
                "ACTIVE"
        );

        userRepository.save(admin);
    }
}