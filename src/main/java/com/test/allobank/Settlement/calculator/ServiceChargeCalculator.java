package com.test.allobank.Settlement.calculator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ServiceChargeCalculator {

    @Value("${app.github-username}")
    private String githubUsername;

    public int calculatePercentage() {

        int asciiSum = githubUsername
                .toLowerCase()
                .chars()
                .sum();

        return asciiSum % 10;
    }

    public BigDecimal calculateAmount(BigDecimal totalExpense) {

        return totalExpense.multiply(
                        BigDecimal.valueOf(calculatePercentage())
                )
                .divide(
                        BigDecimal.valueOf(100),
                        2,
                        RoundingMode.HALF_UP
                );
    }
}