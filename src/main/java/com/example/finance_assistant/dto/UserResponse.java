package com.example.finance_assistant.dto;

public record UserResponse(
        Long id,
        String name,
        String email
) {
}