package com.example.finance_assistant.repository;

import com.example.finance_assistant.dto.CategoryTotal;
import com.example.finance_assistant.entity.Transaction;
import com.example.finance_assistant.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByUserId(Long userId, Pageable pageable);

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    long countByUserIdAndTxnDateBetween(Long userId, LocalDate start, LocalDate end);

    @Query("""
            select sum(t.amount)
            from Transaction t
            where t.user.id = :userId
              and t.type = :type
              and t.txnDate between :start and :end
            """)
    BigDecimal sumAmount(@Param("userId") Long userId,
                         @Param("type") TransactionType type,
                         @Param("start") LocalDate start,
                         @Param("end") LocalDate end);

    @Query("""
            select new com.example.finance_assistant.dto.CategoryTotal(c.id, c.name, sum(t.amount))
            from Transaction t
            join t.category c
            where t.user.id = :userId
              and t.type = :type
              and t.txnDate between :start and :end
            group by c.id, c.name
            order by sum(t.amount) desc
            """)
    List<CategoryTotal> totalsByCategory(@Param("userId") Long userId,
                                         @Param("type") TransactionType type,
                                         @Param("start") LocalDate start,
                                         @Param("end") LocalDate end);

    @Query("""
            select sum(t.amount)
            from Transaction t
            where t.user.id = :userId
              and t.category.id = :categoryId
              and t.type = :type
              and t.txnDate between :start and :end
            """)
    BigDecimal sumByCategory(@Param("userId") Long userId,
                             @Param("categoryId") Long categoryId,
                             @Param("type") TransactionType type,
                             @Param("start") LocalDate start,
                             @Param("end") LocalDate end);
}
