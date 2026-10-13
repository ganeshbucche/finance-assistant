package com.example.finance_assistant.service;

import com.example.finance_assistant.dto.PageResponse;
import com.example.finance_assistant.dto.TransactionRequest;
import com.example.finance_assistant.dto.TransactionResponse;
import com.example.finance_assistant.entity.Category;
import com.example.finance_assistant.entity.Transaction;
import com.example.finance_assistant.entity.User;
import com.example.finance_assistant.exception.BadRequestException;
import com.example.finance_assistant.exception.ResourceNotFoundException;
import com.example.finance_assistant.repository.CategoryRepository;
import com.example.finance_assistant.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public TransactionResponse create(User user, TransactionRequest request) {
        Category category = findCategory(request);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        apply(transaction, request, category);

        return toResponse(transactionRepository.save(transaction));
    }

    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> getAll(User user, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        PageRequest pageable = PageRequest.of(safePage, safeSize,
                Sort.by(Sort.Direction.DESC, "txnDate", "id"));

        Page<Transaction> result = transactionRepository.findByUserId(user.getId(), pageable);

        return new PageResponse<>(
                result.getContent().stream().map(this::toResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public TransactionResponse getById(User user, Long id) {
        return toResponse(findOwned(user, id));
    }

    @Transactional
    public TransactionResponse update(User user, Long id, TransactionRequest request) {
        Transaction transaction = findOwned(user, id);
        Category category = findCategory(request);
        apply(transaction, request, category);
        return toResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public void delete(User user, Long id) {
        transactionRepository.delete(findOwned(user, id));
    }

    private Transaction findOwned(User user, Long id) {
        return transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id " + id));
    }

    private Category findCategory(TransactionRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id " + request.categoryId()));

        if (category.getType() != request.type()) {
            throw new BadRequestException("Category '" + category.getName()
                    + "' is a " + category.getType() + " category, but the transaction type is "
                    + request.type());
        }
        return category;
    }

    private void apply(Transaction transaction, TransactionRequest request, Category category) {
        transaction.setCategory(category);
        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        transaction.setDescription(request.description());
        transaction.setPaymentMode(request.paymentMode());
        transaction.setTxnDate(request.txnDate());
    }

    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getAmount(),
                t.getType(),
                t.getCategory().getId(),
                t.getCategory().getName(),
                t.getDescription(),
                t.getPaymentMode(),
                t.getTxnDate(),
                t.getCreatedAt());
    }
}