package com.example.finance_assistant.dto;

import com.example.finance_assistant.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,

        @NotNull(message = "Type is required (INCOME or EXPENSE)")
        TransactionType type,

        @NotNull(message = "Category id is required")
        Long categoryId,

        @Size(max = 255, message = "Description must be at most 255 characters")
        String description,

        @Size(max = 30, message = "Payment mode must be at most 30 characters")
        String paymentMode,

        @NotNull(message = "Date is required (yyyy-MM-dd)")
        LocalDate txnDate
) {
}