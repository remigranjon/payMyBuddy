package com.paymybuddy.backend.repository.interfaces;

import com.paymybuddy.backend.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
}
