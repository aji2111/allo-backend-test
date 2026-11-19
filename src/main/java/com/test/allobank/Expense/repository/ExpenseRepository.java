package com.test.allobank.Expense.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.test.allobank.Expense.entity.Expense;
import com.test.allobank.Group.entity.BillGroup;

import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    boolean existsByPublicId(UUID publicId);
    List<Expense> findAllByGroup(BillGroup group);
}