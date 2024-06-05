package com.bnabd.bank.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findTop5ByUserIdOrderByCreatedAtDesc(Integer userId);

    Page<Transaction> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
}
