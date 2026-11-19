package com.test.allobank.Expense.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.test.allobank.Expense.dto.request.AddExpenseRequest;
import com.test.allobank.Expense.dto.response.ExpenseResponse;
import com.test.allobank.Expense.service.ExpenseService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
@Tag(name = "Expense", description = "Expense management APIs")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping("/{groupPublicId}/expenses")
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse addExpense(
            @PathVariable UUID groupPublicId,
            @Valid @RequestBody AddExpenseRequest request
    ) {
        return expenseService.addExpense(groupPublicId, request);
    }
}