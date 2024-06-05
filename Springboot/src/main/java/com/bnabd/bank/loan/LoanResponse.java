package com.bnabd.bank.loan;

import com.bnabd.bank.transaction.TransactionType;
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
public class LoanResponse {
    private LoanStatus status;
    private String type;
    private Integer length;
    private BigDecimal amount;
    private BigDecimal paymentLeft;
    private BigDecimal monthlyRate;

}
