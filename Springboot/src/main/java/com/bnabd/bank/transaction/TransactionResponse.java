package com.bnabd.bank.transaction;

import com.bnabd.bank.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private TransactionType type;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private String recipient;
    private String description;

}

