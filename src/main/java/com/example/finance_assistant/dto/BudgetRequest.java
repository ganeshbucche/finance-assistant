package com.example.finance_assistant.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record BudgetRequest(
        @NotNull(message = "Category id is required")
        Long categoryId,

        @NotBlank(message = "Month is required (yyyy-MM)")
        @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])",
                message = "Month must be in yyyy-MM format, for example 2026-10")
        String month,

        @NotNull(message = "Limit amount is required")
        @DecimalMin(value = "0.01", message = "Limit must be greater than 0")
        BigDecimal limitAmount
) {
}