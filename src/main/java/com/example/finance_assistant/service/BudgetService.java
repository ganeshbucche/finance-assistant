package com.example.finance_assistant.service;

import com.example.finance_assistant.dto.BudgetLimitRequest;
import com.example.finance_assistant.dto.BudgetRequest;
import com.example.finance_assistant.dto.BudgetResponse;
import com.example.finance_assistant.entity.Budget;
import com.example.finance_assistant.entity.Category;
import com.example.finance_assistant.entity.Transaction;
import com.example.finance_assistant.entity.TransactionType;
import com.example.finance_assistant.entity.User;
import com.example.finance_assistant.exception.BadRequestException;
import com.example.finance_assistant.exception.DuplicateResourceException;
import com.example.finance_assistant.exception.ResourceNotFoundException;
import com.example.finance_assistant.repository.BudgetRepository;
import com.example.finance_assistant.repository.CategoryRepository;
import com.example.finance_assistant.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal WARNING_PERCENT = BigDecimal.valueOf(80);

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public BudgetResponse create(User user, BudgetRequest request) {
        YearMonth month = parseMonth(request.month());

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id " + request.categoryId()));

        if (category.getType() != TransactionType.EXPENSE) {
            throw new BadRequestException("Budgets can only be set for EXPENSE categories");
        }

        if (budgetRepository.findByUserIdAndCategoryIdAndMonthYear(
                user.getId(), category.getId(), month.toString()).isPresent()) {
            throw new DuplicateResourceException(
                    "A budget for " + category.getName() + " in " + month + " already exists");
        }

        Budget budget = new Budget();
        budget.setUser(user);
        budget.setCategory(category);
        budget.setMonthYear(month.toString());
        budget.setLimitAmount(request.limitAmount());

        return toResponse(budgetRepository.save(budget));
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getAll(User user, String monthText) {
        YearMonth month = (monthText == null || monthText.isBlank())
                ? YearMonth.now()
                : parseMonth(monthText);

        return budgetRepository.findByUserIdAndMonthYear(user.getId(), month.toString())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public BudgetResponse updateLimit(User user, Long id, BudgetLimitRequest request) {
        Budget budget = findOwned(user, id);
        budget.setLimitAmount(request.limitAmount());
        return toResponse(budgetRepository.save(budget));
    }

    @Transactional
    public void delete(User user, Long id) {
        budgetRepository.delete(findOwned(user, id));
    }

    /**
     * Called after an expense is saved. Returns a warning message, or null if no alert is needed.
     */
    public String checkAlert(Long userId, Transaction transaction) {
        if (transaction.getType() != TransactionType.EXPENSE) {
            return null;
        }

        YearMonth month = YearMonth.from(transaction.getTxnDate());
        Budget budget = budgetRepository.findByUserIdAndCategoryIdAndMonthYear(
                userId, transaction.getCategory().getId(), month.toString()).orElse(null);

        if (budget == null) {
            return null;
        }

        BigDecimal spent = spentFor(userId, budget.getCategory().getId(), month);
        BigDecimal limit = budget.getLimitAmount();
        String status = statusOf(spent, limit);
        String name = transaction.getCategory().getName();

        if (status.equals("EXCEEDED")) {
            return "Budget exceeded for " + name + " in " + month
                    + ": spent " + spent + " of " + limit;
        }
        if (status.equals("WARNING")) {
            return "Warning: you have used " + percentOf(spent, limit) + "% of your "
                    + name + " budget for " + month;
        }
        return null;
    }

    private Budget findOwned(User user, Long id) {
        return budgetRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id " + id));
    }

    private BudgetResponse toResponse(Budget budget) {
        YearMonth month = YearMonth.parse(budget.getMonthYear());
        BigDecimal spent = spentFor(budget.getUser().getId(), budget.getCategory().getId(), month);
        BigDecimal limit = budget.getLimitAmount();

        return new BudgetResponse(
                budget.getId(),
                budget.getCategory().getId(),
                budget.getCategory().getName(),
                budget.getMonthYear(),
                limit,
                spent,
                limit.subtract(spent),
                percentOf(spent, limit),
                statusOf(spent, limit));
    }

    private BigDecimal spentFor(Long userId, Long categoryId, YearMonth month) {
        BigDecimal spent = transactionRepository.sumByCategory(
                userId, categoryId, TransactionType.EXPENSE, month.atDay(1), month.atEndOfMonth());
        return spent == null ? BigDecimal.ZERO : spent;
    }

    private BigDecimal percentOf(BigDecimal spent, BigDecimal limit) {
        return spent.multiply(HUNDRED).divide(limit, 1, RoundingMode.HALF_UP);
    }

    private String statusOf(BigDecimal spent, BigDecimal limit) {
        if (spent.compareTo(limit) >= 0) {
            return "EXCEEDED";
        }
        if (spent.multiply(HUNDRED).compareTo(limit.multiply(WARNING_PERCENT)) >= 0) {
            return "WARNING";
        }
        return "OK";
    }

    private YearMonth parseMonth(String text) {
        try {
            return YearMonth.parse(text.trim());
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Month must be in yyyy-MM format, for example 2026-10");
        }
    }
}