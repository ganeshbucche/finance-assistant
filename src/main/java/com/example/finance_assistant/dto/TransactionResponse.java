package com.example.finance_assistant.dto;

import com.example.finance_assistant.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        TransactionType type,
        Long categoryId,
        String categoryName,
        String description,
        String paymentMode,
        LocalDate txnDate,
        LocalDateTime createdAt,
        String budgetAlert
) {
}