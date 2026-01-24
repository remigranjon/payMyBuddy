package com.paymybuddy.backend.model.response;

import com.paymybuddy.backend.model.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private User receiver;
    private double amount;
    private String description;
}
