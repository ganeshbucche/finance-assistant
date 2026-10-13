package com.example.finance_assistant.controller;

import com.example.finance_assistant.dto.BudgetLimitRequest;
import com.example.finance_assistant.dto.BudgetRequest;
import com.example.finance_assistant.dto.BudgetResponse;
import com.example.finance_assistant.entity.User;
import com.example.finance_assistant.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BudgetResponse create(@AuthenticationPrincipal User user,
                                 @Valid @RequestBody BudgetRequest request) {
        return budgetService.create(user, request);
    }

    @GetMapping
    public List<BudgetResponse> getAll(@AuthenticationPrincipal User user,
                                       @RequestParam(required = false) String month) {
        return budgetService.getAll(user, month);
    }

    @PutMapping("/{id}")
    public BudgetResponse updateLimit(@AuthenticationPrincipal User user,
                                      @PathVariable Long id,
                                      @Valid @RequestBody BudgetLimitRequest request) {
        return budgetService.updateLimit(user, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        budgetService.delete(user, id);
    }
}