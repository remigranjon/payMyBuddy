package com.paymybuddy.backend.repository.interfaces;

import com.paymybuddy.backend.model.entity.Transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
    List<Transaction> findBySenderId(int senderId);
}
