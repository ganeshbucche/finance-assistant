package com.example.finance_assistant.dto;

import com.example.finance_assistant.entity.TransactionType;

public record CategoryResponse(
        Long id,
        String name,
        TransactionType type
) {
}