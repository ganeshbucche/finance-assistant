package com.example.finance_assistant.repository;

import com.example.finance_assistant.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserIdAndMonthYear(Long userId, String monthYear);

    Optional<Budget> findByIdAndUserId(Long id, Long userId);

    Optional<Budget> findByUserIdAndCategoryIdAndMonthYear(Long userId, Long categoryId, String monthYear);
}