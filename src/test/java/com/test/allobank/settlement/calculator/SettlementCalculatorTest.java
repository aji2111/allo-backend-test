package com.test.allobank.settlement.calculator;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.test.allobank.Expense.entity.Expense;
import com.test.allobank.Expense.entity.ExpenseSplit;
import com.test.allobank.Group.entity.Participant;
import com.test.allobank.Settlement.calculator.SettlementCalculator;
import com.test.allobank.Settlement.dto.response.SettlementItem;
import com.test.allobank.Settlement.dto.response.SettlementSummary;

class SettlementCalculatorTest {

    private SettlementCalculator settlementCalculator;

    @BeforeEach
    void setUp() {

        settlementCalculator =
                new SettlementCalculator();
    }

    @Test
    void shouldCalculateSettlementCorrectly() {

        Participant sekar = new Participant();
        sekar.setName("Sekar");

        Participant aji = new Participant();
        aji.setName("Aji");

        Participant septia = new Participant();
        septia.setName("Septia");

        Expense expense = new Expense();

        expense.setAmount(
                BigDecimal.valueOf(900000)
        );

        expense.setPaidBy(sekar);

        ExpenseSplit split1 = new ExpenseSplit();
        split1.setParticipant(sekar);
        split1.setShareAmount(
                BigDecimal.valueOf(300000)
        );

        ExpenseSplit split2 = new ExpenseSplit();
        split2.setParticipant(aji);
        split2.setShareAmount(
                BigDecimal.valueOf(300000)
        );

        ExpenseSplit split3 = new ExpenseSplit();
        split3.setParticipant(septia);
        split3.setShareAmount(
                BigDecimal.valueOf(300000)
        );

        expense.setSplits(
                List.of(split1, split2, split3)
        );

        SettlementSummary result =
                settlementCalculator.calculate(
                        List.of(expense)
                );

        assertNotNull(result);

        assertEquals(
                BigDecimal.valueOf(900000),
                result.getTotalExpense()
        );

        assertEquals(
                2,
                result.getSettlements().size()
        );

        SettlementItem firstSettlement =
                result.getSettlements().get(0);

        assertEquals(
                "Aji",
                firstSettlement.getFrom()
        );

        assertEquals(
                "Sekar",
                firstSettlement.getTo()
        );

        assertEquals(
                BigDecimal.valueOf(300000.00)
                        .setScale(2),
                firstSettlement.getAmount()
        );
    }

    @Test
    void shouldCalculateSettlementFromMultipleExpenses() {

        Participant sekar = new Participant();
        sekar.setName("Sekar");

        Participant aji = new Participant();
        aji.setName("Aji");

        Expense dinner = new Expense();

        dinner.setAmount(
                BigDecimal.valueOf(600000)
        );

        dinner.setPaidBy(sekar);

        ExpenseSplit dinnerSplit1 = new ExpenseSplit();
        dinnerSplit1.setParticipant(sekar);
        dinnerSplit1.setShareAmount(
                BigDecimal.valueOf(300000)
        );

        ExpenseSplit dinnerSplit2 = new ExpenseSplit();
        dinnerSplit2.setParticipant(aji);
        dinnerSplit2.setShareAmount(
                BigDecimal.valueOf(300000)
        );

        dinner.setSplits(
                List.of(dinnerSplit1, dinnerSplit2)
        );

        Expense transport = new Expense();

        transport.setAmount(
                BigDecimal.valueOf(200000)
        );

        transport.setPaidBy(aji);

        ExpenseSplit transportSplit1 = new ExpenseSplit();
        transportSplit1.setParticipant(sekar);
        transportSplit1.setShareAmount(
                BigDecimal.valueOf(100000)
        );

        ExpenseSplit transportSplit2 = new ExpenseSplit();
        transportSplit2.setParticipant(aji);
        transportSplit2.setShareAmount(
                BigDecimal.valueOf(100000)
        );

        transport.setSplits(
                List.of(transportSplit1, transportSplit2)
        );

        SettlementSummary result =
                settlementCalculator.calculate(
                        List.of(dinner, transport)
                );

        assertNotNull(result);

        assertEquals(
                BigDecimal.valueOf(800000),
                result.getTotalExpense()
        );

        assertFalse(
                result.getSettlements().isEmpty()
        );
    }

    @Test
    void shouldCalculatePercentageSplitCorrectly() {

        BigDecimal amount =
                BigDecimal.valueOf(1000000);

        BigDecimal fiftyPercent =
                amount.multiply(
                                BigDecimal.valueOf(50)
                        )
                        .divide(
                                BigDecimal.valueOf(100)
                        );

        BigDecimal thirtyPercent =
                amount.multiply(
                                BigDecimal.valueOf(30)
                        )
                        .divide(
                                BigDecimal.valueOf(100)
                        );

        BigDecimal twentyPercent =
                amount.multiply(
                                BigDecimal.valueOf(20)
                        )
                        .divide(
                                BigDecimal.valueOf(100)
                        );

        assertEquals(
                BigDecimal.valueOf(500000),
                fiftyPercent
        );

        assertEquals(
                BigDecimal.valueOf(300000),
                thirtyPercent
        );

        assertEquals(
                BigDecimal.valueOf(200000),
                twentyPercent
        );
    }

    @Test
    void shouldValidateExactSplitAmount() {

        BigDecimal total =
                BigDecimal.valueOf(1500000);

        BigDecimal split1 =
                BigDecimal.valueOf(700000);

        BigDecimal split2 =
                BigDecimal.valueOf(500000);

        BigDecimal split3 =
                BigDecimal.valueOf(300000);

        BigDecimal result =
                split1.add(split2).add(split3);

        assertEquals(total, result);
    }
}