package com.test.allobank.Settlement.service;

import com.test.allobank.Expense.entity.Expense;
import com.test.allobank.Expense.repository.ExpenseRepository;
import com.test.allobank.Group.entity.BillGroup;
import com.test.allobank.Group.repository.BillGroupRepository;
import com.test.allobank.Settlement.calculator.ServiceChargeCalculator;
import com.test.allobank.Settlement.calculator.SettlementCalculator;
import com.test.allobank.Settlement.dto.response.SettlementResponse;
import com.test.allobank.Settlement.dto.response.SettlementSummary;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementService {

    private final BillGroupRepository billGroupRepository;
    private final ExpenseRepository expenseRepository;
    private final SettlementCalculator settlementCalculator;
    private final ServiceChargeCalculator serviceChargeCalculator;

    /**
     * Generate settlement summary for group.
     */
    public SettlementResponse getSettlement(
            UUID groupPublicId
    ) {

        log.info(
                "Generating settlement for groupId={}",
                groupPublicId
        );

        BillGroup group = getGroup(groupPublicId);

        List<Expense> expenses =
                expenseRepository.findAllByGroup(group);

        SettlementSummary summary =
                settlementCalculator.calculate(expenses);

        SettlementResponse response =
                SettlementResponse.builder()
                        .groupId(group.getPublicId())
                        .groupName(group.getName())
                        .totalExpense(summary.getTotalExpense())
                        .serviceChargePct(
                                serviceChargeCalculator
                                        .calculatePercentage()
                        )
                        .serviceChargeAmount(
                                serviceChargeCalculator
                                        .calculateAmount(
                                                summary.getTotalExpense()
                                        )
                        )
                        .settlements(summary.getSettlements())
                        .build();

        log.info(
                "Settlement generated successfully for groupId={}",
                groupPublicId
        );

        return response;
    }

    private BillGroup getGroup(
            UUID groupPublicId
    ) {

        return billGroupRepository.findByPublicId(groupPublicId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Group not found"
                        )
                );
    }
}