package com.test.allobank.Expense.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ExpenseResponse {

    private UUID publicId;

    private String title;

    private BigDecimal amount;

    private String paidBy;

    private LocalDateTime createdAt;
}
