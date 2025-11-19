package com.company.university.security.service;

import com.company.university.security.domain.Role;
import com.company.university.security.domain.User;
import com.company.university.security.dto.AuthRequest;
import com.company.university.security.dto.AuthResponse;
import com.company.university.security.jwt.JwtService;
import com.company.university.security.repository.RoleRepository;
import com.company.university.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(String username, String password, Set<String> roleNames) {
        var roles = roleNames.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        var user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(roles)
                .build();

        userRepository.save(user);

        var token = jwtService.generateToken(username, roleNames);
        return new AuthResponse(token);
    }

    public AuthResponse login(AuthRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        var roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        var token = jwtService.generateToken(user.getUsername(), roles);

        return new AuthResponse(token);
    }
}