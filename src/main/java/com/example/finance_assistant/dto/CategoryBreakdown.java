package com.example.finance_assistant.dto;

import java.math.BigDecimal;

public record CategoryBreakdown(
        Long categoryId,
        String categoryName,
        BigDecimal total,
        BigDecimal percentage
) {
}