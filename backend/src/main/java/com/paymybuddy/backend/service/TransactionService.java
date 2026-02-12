package com.paymybuddy.backend.service;

import org.springframework.stereotype.Service;

import com.paymybuddy.backend.model.entity.Transaction;
import com.paymybuddy.backend.model.entity.User;
import com.paymybuddy.backend.model.request.TransactionRequest;
import com.paymybuddy.backend.model.response.TransactionResponse;
import com.paymybuddy.backend.repository.interfaces.TransactionRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    public TransactionService(TransactionRepository transactionRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    @Transactional
    public void saveTransaction(TransactionRequest transactionRequest, int senderId) throws Exception {
        User sender = userService.findById(senderId);
        User receiver = userService.findById(transactionRequest.getReceiverId());
        Transaction transaction = Transaction.builder()
                .sender(sender)
                .receiver(receiver)
                .amount(transactionRequest.getAmount())
                .description(transactionRequest.getDescription())
                .build();
        try {
            transactionRepository.save(transaction);
        } catch (Exception e) {
            log.error("Error saving transaction: {}", e.getMessage());
            throw new RuntimeException("Error saving transaction");
        }
        try {
            userService.addCredit(sender.getUsername(), -transactionRequest.getAmount());
            userService.addCredit(receiver.getUsername(), transactionRequest.getAmount());
        } catch (Exception e) {
            log.error("Error updating sender credit: {}", e.getMessage());
            throw new RuntimeException("Error updating sender credit");
        }
        log.info("Transaction saved: {} -> {} : {}", sender.getUsername(), receiver.getUsername(),
                transactionRequest.getAmount());
    }

    public Iterable<TransactionResponse> getTransactionsForUser(int userId) {
        return transactionRepository.findBySenderId(userId).stream().map(transaction -> TransactionResponse.builder()
                .receiver(transaction.getReceiver())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .build()).toList();
    }
}
