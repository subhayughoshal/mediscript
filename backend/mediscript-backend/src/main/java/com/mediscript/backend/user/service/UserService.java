package com.mediscript.backend.user.service;

import com.mediscript.backend.user.entity.Role;
import com.mediscript.backend.user.entity.User;
import com.mediscript.backend.user.repository.RoleRepository;
import com.mediscript.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public User createUser(
            String name,
            String email,
            String password,
            String phone,
            String roleName
    ) {

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new RuntimeException("Role not found: " + roleName)
                );

        User user = new User(
                name,
                email,
                password,
                phone,
                role,
                "ACTIVE"
        );

        return userRepository.save(user);
    }
}