package com.bnabd.bank.transaction;

public enum TransactionType {
    DEPOSIT, // WPŁATA
    ATM_DEPOSIT, // WPŁATA Z WPŁATOMATU
    USER_TRANSFER, // WPŁYW OD INNEGO UŻYTKOWNIKA
    WITHDRAW, // WYPŁATA
    TRANSFER, // PRZELEW
    LOAN, // POŻYCZKA
    INTEREST, // ODSETKI
    MONTHLY_RATE, // MIESIĘCZNA RATA
    LOAN_PAYMENT // SPŁATA POŻYCZKI
}