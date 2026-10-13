package com.example.finance_assistant.controller;

import com.example.finance_assistant.dto.PageResponse;
import com.example.finance_assistant.dto.TransactionRequest;
import com.example.finance_assistant.dto.TransactionResponse;
import com.example.finance_assistant.entity.User;
import com.example.finance_assistant.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@AuthenticationPrincipal User user,
                                      @Valid @RequestBody TransactionRequest request) {
        return transactionService.create(user, request);
    }

    @GetMapping
    public PageResponse<TransactionResponse> getAll(@AuthenticationPrincipal User user,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        return transactionService.getAll(user, page, size);
    }

    @GetMapping("/{id}")
    public TransactionResponse getById(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return transactionService.getById(user, id);
    }

    @PutMapping("/{id}")
    public TransactionResponse update(@AuthenticationPrincipal User user,
                                      @PathVariable Long id,
                                      @Valid @RequestBody TransactionRequest request) {
        return transactionService.update(user, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        transactionService.delete(user, id);
    }
}