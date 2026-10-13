package com.example.finance_assistant.dto;

import java.math.BigDecimal;
import java.util.List;

public record MonthlySummaryResponse(
        String month,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal savings,
        BigDecimal savingsRatePercent,
        long transactionCount,
        List<CategoryBreakdown> expenseByCategory,
        List<CategoryBreakdown> incomeByCategory
) {
}