package com.test.allobank.Settlement.calculator;
import org.springframework.stereotype.Component;

import com.test.allobank.Expense.entity.Expense;
import com.test.allobank.Expense.entity.ExpenseSplit;
import com.test.allobank.Settlement.dto.response.SettlementItem;
import com.test.allobank.Settlement.dto.response.SettlementSummary;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component
public class SettlementCalculator {

    public SettlementSummary calculate(List<Expense> expenses) {

        Map<String, BigDecimal> paid = new HashMap<>();
        Map<String, BigDecimal> owed = new HashMap<>();

        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Expense expense : expenses) {

            totalExpense = totalExpense.add(expense.getAmount());

            addPaidAmount(paid, expense);

            addOwedAmount(owed, expense);
        }

        Map<String, BigDecimal> balances =
                calculateBalances(paid, owed);

        List<SettlementItem> settlements =
                buildSettlements(balances);

        return SettlementSummary.builder()
                .totalExpense(totalExpense)
                .settlements(settlements)
                .build();
    }

    private void addPaidAmount(
            Map<String, BigDecimal> paid,
            Expense expense
    ) {

        String payer = expense.getPaidBy().getName();

        paid.put(
                payer,
                paid.getOrDefault(payer, BigDecimal.ZERO)
                        .add(expense.getAmount())
        );
    }

    private void addOwedAmount(
            Map<String, BigDecimal> owed,
            Expense expense
    ) {

        for (ExpenseSplit split : expense.getSplits()) {

            String participant =
                    split.getParticipant().getName();

            owed.put(
                    participant,
                    owed.getOrDefault(participant, BigDecimal.ZERO)
                            .add(split.getShareAmount())
            );
        }
    }

    private Map<String, BigDecimal> calculateBalances(
            Map<String, BigDecimal> paid,
            Map<String, BigDecimal> owed
    ) {

        Set<String> participants = new HashSet<>();

        participants.addAll(paid.keySet());
        participants.addAll(owed.keySet());

        Map<String, BigDecimal> balances = new HashMap<>();

        for (String participant : participants) {

            BigDecimal totalPaid =
                    paid.getOrDefault(participant, BigDecimal.ZERO);

            BigDecimal totalOwed =
                    owed.getOrDefault(participant, BigDecimal.ZERO);

            balances.put(
                    participant,
                    totalPaid.subtract(totalOwed)
            );
        }

        return balances;
    }

    private List<SettlementItem> buildSettlements(
            Map<String, BigDecimal> balances
    ) {

        List<Map.Entry<String, BigDecimal>> creditors =
                balances.entrySet()
                        .stream()
                        .filter(entry ->
                                entry.getValue()
                                        .compareTo(BigDecimal.ZERO) > 0
                        )
                        .toList();

        List<Map.Entry<String, BigDecimal>> debtors =
                balances.entrySet()
                        .stream()
                        .filter(entry ->
                                entry.getValue()
                                        .compareTo(BigDecimal.ZERO) < 0
                        )
                        .toList();

        List<SettlementItem> settlements = new ArrayList<>();

        int creditorIndex = 0;

        for (Map.Entry<String, BigDecimal> debtor : debtors) {

            BigDecimal debt = debtor.getValue().abs();

            while (
                    debt.compareTo(BigDecimal.ZERO) > 0
                            && creditorIndex < creditors.size()
            ) {

                Map.Entry<String, BigDecimal> creditor =
                        creditors.get(creditorIndex);

                BigDecimal credit = creditor.getValue();

                BigDecimal amount = debt.min(credit);

                settlements.add(
                        SettlementItem.builder()
                                .from(debtor.getKey())
                                .to(creditor.getKey())
                                .amount(
                                        amount.setScale(
                                                2,
                                                RoundingMode.HALF_UP
                                        )
                                )
                                .build()
                );

                debt = debt.subtract(amount);

                creditor.setValue(
                        credit.subtract(amount)
                );

                if (
                        creditor.getValue()
                                .compareTo(BigDecimal.ZERO) == 0
                ) {
                    creditorIndex++;
                }
            }
        }

        return settlements;
    }
}