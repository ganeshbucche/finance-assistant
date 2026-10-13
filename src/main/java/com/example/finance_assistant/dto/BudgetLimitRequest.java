package com.example.finance_assistant.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BudgetLimitRequest(
        @NotNull(message = "Limit amount is required")
        @DecimalMin(value = "0.01", message = "Limit must be greater than 0")
        BigDecimal limitAmount
) {
}