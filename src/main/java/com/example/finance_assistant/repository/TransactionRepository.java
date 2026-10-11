package com.example.finance_assistant.repository;

import com.example.finance_assistant.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}