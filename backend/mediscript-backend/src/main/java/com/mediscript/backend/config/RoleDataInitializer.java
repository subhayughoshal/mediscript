package com.mediscript.backend.config;

import com.mediscript.backend.user.entity.Role;
import com.mediscript.backend.user.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleDataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleDataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {

        createRoleIfNotExists("ADMIN");
        createRoleIfNotExists("DOCTOR");
        createRoleIfNotExists("PATIENT");
        createRoleIfNotExists("RECEPTIONIST");
    }

    private void createRoleIfNotExists(String roleName) {

        boolean exists = roleRepository
                .findAll()
                .stream()
                .anyMatch(role -> role.getName().equals(roleName));

        if (!exists) {
            roleRepository.save(new Role(roleName));
        }
    }
}