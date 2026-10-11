package com.example.finance_assistant.dto;

import com.example.finance_assistant.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 50, message = "Name must be at most 50 characters")
        String name,

        @NotNull(message = "Type is required (INCOME or EXPENSE)")
        TransactionType type
) {
}