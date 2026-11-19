package com.test.allobank.Settlement.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class SettlementResponse {

    private UUID groupId;

    private String groupName;

    private BigDecimal totalExpense;

    private Integer serviceChargePct;

    private BigDecimal serviceChargeAmount;

    private List<SettlementItem> settlements;
}