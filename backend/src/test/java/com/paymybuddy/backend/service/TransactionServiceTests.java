package com.paymybuddy.backend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.paymybuddy.backend.model.entity.Transaction;
import com.paymybuddy.backend.model.entity.User;
import com.paymybuddy.backend.model.request.TransactionRequest;
import com.paymybuddy.backend.model.response.TransactionResponse;
import com.paymybuddy.backend.repository.interfaces.TransactionRepository;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class TransactionServiceTests {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionService transactionService;


    private User createUser1() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setEmail("user1@example.com");
        user.setPassword("password");
        user.setCredit(100.0);
        return user;
    }

    private User createUser2() {
        User user = new User();
        user.setId(2L);
        user.setUsername("user2");
        user.setEmail("user2@example.com");
        user.setPassword("password");
        user.setCredit(100.0);
        return user;
    }

    @Test
    public void testSaveTransactionSuccess()  {
        when(userService.findById(1)).thenReturn(createUser1());
        when(userService.findById(2)).thenReturn(createUser2());
        try {
        transactionService.saveTransaction(
                TransactionRequest.builder()
                        .receiverId(2)
                        .amount(50.0)
                        .description("Test transaction")
                        .build(),
                1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        verify(transactionRepository,Mockito.times(1)).save(any(Transaction.class));
        try {
            verify(userService, Mockito.times(1)).addCredit("user2", 50.0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            verify(userService, Mockito.times(1)).addCredit("user1", -50.0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSaveTransactionUserNotFound() {
        when(userService.findById(1)).thenThrow(new NoSuchElementException("User not found"));
        try {
            transactionService.saveTransaction(
                    TransactionRequest.builder()
                            .receiverId(2)
                            .amount(50.0)
                            .description("Test transaction")
                            .build(),
                    1);
        } catch (Exception e) {
            assert(e.getMessage().equals("Error finding users"));
        }
    }

    @Test
    public void testSaveTransactionErrorSaving() {
        when(userService.findById(1)).thenReturn(createUser1());
        when(userService.findById(2)).thenReturn(createUser2());
        when(transactionRepository.save(any(Transaction.class))).thenThrow(new RuntimeException("Error saving transaction"));
        try {
            transactionService.saveTransaction(
                    TransactionRequest.builder()
                            .receiverId(2)
                            .amount(50.0)
                            .description("Test transaction")
                            .build(),
                    1);
        } catch (Exception e) {
            assert(e.getMessage().equals("Error saving transaction"));
        }
    }

    @Test
    public void testSaveTransactionErrorUpdatingCredit()  {
        when(userService.findById(1)).thenReturn(createUser1());
        when(userService.findById(2)).thenReturn(createUser2());
        try {
            doThrow(new Exception("Error updating sender credit"))
                .when(userService).addCredit("user1", -50.0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            transactionService.saveTransaction(
                    TransactionRequest.builder()
                            .receiverId(2)
                            .amount(50.0)
                            .description("Test transaction")
                            .build(),
                    1);
        } catch (Exception e) {
            assert(e.getMessage().equals("Error updating sender credit"));
        }
    }

    @Test
    public void testGetTransactionsForUser() {
        when(transactionRepository.findBySenderId(1)).thenReturn(java.util.List.of(
                Transaction.builder()
                        .receiver(createUser2())
                        .amount(50.0)
                        .description("Test transaction")
                        .build()
        ));
        Iterable<TransactionResponse> responses = transactionService.getTransactionsForUser(1);
        assert(responses.iterator().hasNext());
        TransactionResponse response = responses.iterator().next();
        assert(response.getReceiver().equals(createUser2()));
        assert(response.getAmount() == 50.0);
        assert(response.getDescription().equals("Test transaction"));
    }
   
}
