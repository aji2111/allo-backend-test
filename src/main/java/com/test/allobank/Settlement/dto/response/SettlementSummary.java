package com.test.allobank.Settlement.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class SettlementSummary {

    private BigDecimal totalExpense;

    private List<SettlementItem> settlements;
}