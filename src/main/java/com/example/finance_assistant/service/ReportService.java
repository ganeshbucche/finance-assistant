package com.example.finance_assistant.service;

import com.example.finance_assistant.dto.CategoryBreakdown;
import com.example.finance_assistant.dto.CategoryTotal;
import com.example.finance_assistant.dto.MonthlySummaryResponse;
import com.example.finance_assistant.entity.TransactionType;
import com.example.finance_assistant.entity.User;
import com.example.finance_assistant.exception.BadRequestException;
import com.example.finance_assistant.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public MonthlySummaryResponse monthlySummary(User user, String monthText) {
        YearMonth month = parseMonth(monthText);
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        Long userId = user.getId();

        BigDecimal income = orZero(transactionRepository.sumAmount(userId, TransactionType.INCOME, start, end));
        BigDecimal expense = orZero(transactionRepository.sumAmount(userId, TransactionType.EXPENSE, start, end));
        BigDecimal savings = income.subtract(expense);

        BigDecimal savingsRate = income.signum() > 0
                ? savings.multiply(BigDecimal.valueOf(100)).divide(income, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long count = transactionRepository.countByUserIdAndTxnDateBetween(userId, start, end);

        List<CategoryBreakdown> expenseBreakdown = toBreakdown(
                transactionRepository.totalsByCategory(userId, TransactionType.EXPENSE, start, end), expense);
        List<CategoryBreakdown> incomeBreakdown = toBreakdown(
                transactionRepository.totalsByCategory(userId, TransactionType.INCOME, start, end), income);

        return new MonthlySummaryResponse(month.toString(), income, expense, savings,
                savingsRate, count, expenseBreakdown, incomeBreakdown);
    }

    private YearMonth parseMonth(String text) {
        if (text == null || text.isBlank()) {
            return YearMonth.now();
        }
        try {
            return YearMonth.parse(text.trim());
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Month must be in yyyy-MM format, for example 2026-10");
        }
    }

    private List<CategoryBreakdown> toBreakdown(List<CategoryTotal> totals, BigDecimal grandTotal) {
        return totals.stream()
                .map(t -> new CategoryBreakdown(
                        t.categoryId(),
                        t.categoryName(),
                        t.total(),
                        grandTotal.signum() > 0
                                ? t.total().multiply(BigDecimal.valueOf(100))
                                .divide(grandTotal, 1, RoundingMode.HALF_UP)
                                : BigDecimal.ZERO))
                .toList();
    }

    private BigDecimal orZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}