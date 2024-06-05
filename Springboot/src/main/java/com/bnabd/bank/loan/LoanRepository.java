package com.bnabd.bank.loan;

import com.bnabd.bank.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Integer> {
    Loan findLoanByUserId(Integer userId);
    Loan findTop1ByUserIdOrderByIdDesc(Integer userId);
    Optional<Loan> findLoanById(Integer id);
    List<Loan> findByStatus(LoanStatus status);
}
