package com.example.finance_assistant.dto;

import java.math.BigDecimal;

public record BudgetResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String month,
        BigDecimal limitAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        BigDecimal percentUsed,
        String status
) {
}