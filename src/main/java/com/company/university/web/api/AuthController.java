package com.company.university.web.api;

import com.company.university.security.dto.AuthRequest;
import com.company.university.security.dto.AuthResponse;
import com.company.university.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam Set<String> roles) {
        return authService.register(username, password, roles);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }
}