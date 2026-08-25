package com.mediscript.backend.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.mediscript.backend.user.entity.Role;
import com.mediscript.backend.user.entity.User;
import com.mediscript.backend.user.repository.RoleRepository;
import com.mediscript.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.mediscript.backend.exception.ConflictException;
import com.mediscript.backend.exception.ResourceNotFoundException;
import com.mediscript.backend.exception.BadRequestException;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(
            String name,
            String email,
            String password,
            String phone,
            String roleName
    ) {

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email already registered");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found: " + roleName)
                );

        User user = new User(
                name,
                email,
                passwordEncoder.encode(password),
                phone,
                role,
                "ACTIVE"
        );

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUserStatus(Long userId, String status) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        if (!"ACTIVE".equalsIgnoreCase(status)
                && !"INACTIVE".equalsIgnoreCase(status)) {
            throw new BadRequestException(
                    "Status must be ACTIVE or INACTIVE"
            );
        }

        user.setStatus(status.toUpperCase());

        return userRepository.save(user);
    }

    public User getUserById(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );
    }

    public User updateUser(
            Long userId,
            String name,
            String email,
            String phone,
            String roleName
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        // Prevent changing to an email already used by another user
        if (!user.getEmail().equalsIgnoreCase(email)
                && userRepository.existsByEmail(email)) {

            throw new ConflictException(
                    "Email already registered"
            );
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role not found: " + roleName
                        )
                );

        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);

        return userRepository.save(user);
    }
}