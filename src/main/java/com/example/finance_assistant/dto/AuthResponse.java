package com.example.finance_assistant.dto;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String name,
        String email
) {
}