package com.test.allobank.Settlement.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class SettlementItem {

    private String from;

    private String to;

    private BigDecimal amount;
}
