package com.example.finance_assistant.controller;

import com.example.finance_assistant.dto.AuthResponse;
import com.example.finance_assistant.dto.LoginRequest;
import com.example.finance_assistant.dto.RegisterRequest;
import com.example.finance_assistant.dto.UserResponse;
import com.example.finance_assistant.entity.User;
import com.example.finance_assistant.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}