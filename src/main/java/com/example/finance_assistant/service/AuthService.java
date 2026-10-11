package com.example.finance_assistant.service;

import com.example.finance_assistant.dto.AuthResponse;
import com.example.finance_assistant.dto.LoginRequest;
import com.example.finance_assistant.dto.RegisterRequest;
import com.example.finance_assistant.entity.User;
import com.example.finance_assistant.exception.DuplicateResourceException;
import com.example.finance_assistant.exception.InvalidCredentialsException;
import com.example.finance_assistant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("Email is already registered");
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return toResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return toResponse(user);
    }

    private AuthResponse toResponse(User user) {
        return new AuthResponse(jwtService.generateToken(user), "Bearer",
                user.getId(), user.getName(), user.getEmail());
    }
}