package com.example.finance_assistant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
@Getter @Setter @NoArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "target_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal targetAmount;

    @Column(name = "saved_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal savedAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDate deadline;

    private LocalDateTime createdAt = LocalDateTime.now();
}