package com.test.allobank.Expense.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.allobank.Expense.dto.request.AddExpenseRequest;
import com.test.allobank.Expense.dto.request.ExpenseSplitRequest;
import com.test.allobank.Expense.dto.response.ExpenseResponse;
import com.test.allobank.Expense.entity.Expense;
import com.test.allobank.Expense.entity.ExpenseSplit;
import com.test.allobank.Expense.repository.ExpenseRepository;
import com.test.allobank.Group.entity.BillGroup;
import com.test.allobank.Group.entity.Participant;
import com.test.allobank.Group.repository.BillGroupRepository;
import com.test.allobank.Group.repository.ParticipantRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BillGroupRepository billGroupRepository;
    private final ParticipantRepository participantRepository;

    /**
     * Add expense into bill group.
     */
    @Transactional
    public ExpenseResponse addExpense(
            UUID groupPublicId,
            AddExpenseRequest request
    ) {

        log.info(
                "Adding expense into groupId={}",
                groupPublicId
        );

        BillGroup group = getGroup(groupPublicId);

        Participant paidBy =
                getParticipant(request.getPaidByParticipantId());

        Expense expense = buildExpense(
                request,
                group,
                paidBy
        );

        createSplits(
                expense,
                request
        );

        Expense savedExpense =
                expenseRepository.save(expense);

        log.info(
                "Expense created successfully. expenseId={}",
                savedExpense.getPublicId()
        );

        return toResponse(savedExpense);
    }

    private BillGroup getGroup(UUID groupPublicId) {

        return billGroupRepository.findByPublicId(groupPublicId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Group not found"
                        )
                );
    }

    private Participant getParticipant(UUID participantId) {

        return participantRepository.findByPublicId(participantId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Participant not found"
                        )
                );
    }

    private Expense buildExpense(
            AddExpenseRequest request,
            BillGroup group,
            Participant paidBy
    ) {

        Expense expense = new Expense();

        expense.setGroup(group);
        expense.setPaidBy(paidBy);
        expense.setTitle(request.getTitle());
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setSplitType(request.getSplitType());

        return expense;
    }

    private void createSplits(
            Expense expense,
            AddExpenseRequest request
    ) {

        switch (request.getSplitType()) {

            case EQUAL ->
                    createEqualSplit(
                            expense,
                            request
                    );

            case EXACT ->
                    createExactSplit(
                            expense,
                            request
                    );

            case PERCENTAGE ->
                    createPercentageSplit(
                            expense,
                            request
                    );
        }
    }

    private void createEqualSplit(
            Expense expense,
            AddExpenseRequest request
    ) {

        validateSplits(request);

        BigDecimal splitAmount =
                calculateSplitAmount(
                        expense.getAmount(),
                        request.getSplits().size()
                );

        for (ExpenseSplitRequest splitRequest :
                request.getSplits()) {

            Participant participant =
                    getParticipant(
                            splitRequest.getParticipantId()
                    );

            ExpenseSplit split = new ExpenseSplit();

            split.setExpense(expense);
            split.setParticipant(participant);
            split.setShareAmount(splitAmount);

            expense.getSplits().add(split);
        }
    }

    private void createExactSplit(
            Expense expense,
            AddExpenseRequest request
    ) {

        validateSplits(request);

        BigDecimal totalSplit = BigDecimal.ZERO;

        for (ExpenseSplitRequest splitRequest :
                request.getSplits()) {

            Participant participant =
                    getParticipant(
                            splitRequest.getParticipantId()
                    );

            ExpenseSplit split = new ExpenseSplit();

            split.setExpense(expense);
            split.setParticipant(participant);
            split.setShareAmount(
                    splitRequest.getAmount()
            );

            expense.getSplits().add(split);

            totalSplit =
                    totalSplit.add(
                            splitRequest.getAmount()
                    );
        }

        if (
                totalSplit.compareTo(
                        expense.getAmount()
                ) != 0
        ) {

            throw new IllegalArgumentException(
                    "Total exact split must equal expense amount"
            );
        }
    }

    private void createPercentageSplit(
            Expense expense,
            AddExpenseRequest request
    ) {

        validateSplits(request);

        BigDecimal totalPercentage = BigDecimal.ZERO;

        for (ExpenseSplitRequest splitRequest :
                request.getSplits()) {

            Participant participant =
                    getParticipant(
                            splitRequest.getParticipantId()
                    );

            BigDecimal splitAmount =
                    expense.getAmount()
                            .multiply(
                                    splitRequest.getPercentage()
                            )
                            .divide(
                                    BigDecimal.valueOf(100),
                                    2,
                                    RoundingMode.HALF_UP
                            );

            ExpenseSplit split = new ExpenseSplit();

            split.setExpense(expense);
            split.setParticipant(participant);
            split.setShareAmount(splitAmount);

            expense.getSplits().add(split);

            totalPercentage =
                    totalPercentage.add(
                            splitRequest.getPercentage()
                    );
        }

        if (
                totalPercentage.compareTo(
                        BigDecimal.valueOf(100)
                ) != 0
        ) {

            throw new IllegalArgumentException(
                    "Total percentage must equal 100"
            );
        }
    }

    private void validateSplits(
            AddExpenseRequest request
    ) {

        if (
                request.getSplits() == null ||
                request.getSplits().isEmpty()
        ) {

            log.warn("Split request is empty");

            throw new IllegalArgumentException(
                    "Splits cannot be empty"
            );
        }
    }

    private BigDecimal calculateSplitAmount(
            BigDecimal amount,
            int participantSize
    ) {

        return amount.divide(
                BigDecimal.valueOf(participantSize),
                2,
                RoundingMode.HALF_UP
        );
    }

    private ExpenseResponse toResponse(
            Expense expense
    ) {

        return ExpenseResponse.builder()
                .publicId(expense.getPublicId())
                .title(expense.getTitle())
                .amount(expense.getAmount())
                .paidBy(expense.getPaidBy().getName())
                .createdAt(expense.getCreatedAt())
                .build();
    }
}