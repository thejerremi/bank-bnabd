package com.bnabd.bank.admin;

import com.bnabd.bank.loan.*;
import com.bnabd.bank.transaction.Transaction;
import com.bnabd.bank.transaction.TransactionRepository;
import com.bnabd.bank.transaction.TransactionType;
import com.bnabd.bank.user.User;
import com.bnabd.bank.user.UserDTO;
import com.bnabd.bank.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    private LoanResponse convertToResponse(Loan loan) {
        return LoanResponse.builder()
                .status(loan.getStatus())
                .type(loan.getType())
                .length(loan.getLength())
                .amount(loan.getAmount())
                .paymentLeft(loan.getPaymentLeft())
                .monthlyRate(loan.getMonthlyRate())
                .build();
    }

    private LoanResponseWrapper convertToWrapperResponse(Loan loan) {
        LoanResponse loanResponse = convertToResponse(loan);
        UserDTO userDTO = convertToUserDTO(loan.getUser());
        return new LoanResponseWrapper(loan.getId(), loanResponse, userDTO);
    }

    public UserDTO convertToUserDTO(User user) {
        return new UserDTO(
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getPesel(),
                user.getDob(),
                user.getBalance(),
                user.getAccountNumber(),
                user.getHasLoan(),
                user.getRole()
        );
    }
    public List<LoanResponseWrapper> findPendingLoans() {
        List<Loan> loans = loanRepository.findByStatus(LoanStatus.PENDING);
        return loans.stream()
                .map(this::convertToWrapperResponse)
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> changeLoanStatus(String decision, Integer loanId) {
        Optional<Loan> possibleLoan = loanRepository.findLoanById(loanId);
        if(possibleLoan.isEmpty()){
            ResponseEntity.badRequest().body("Loan with that id was not found.");
        }
        Loan loan = possibleLoan.get();
        if(Objects.equals(decision, "ACCEPT")){
            loan.setStatus(LoanStatus.ACCEPTED);
            User user = loan.getUser();
            user.setHasLoan(true);
            user.setBalance(user.getBalance().add(loan.getAmount()));
            var transaction = Transaction.builder()
                    .type(TransactionType.LOAN)
                    .amount(loan.getAmount())
                    .user(user)
                    .createdAt(LocalDateTime.now(ZoneId.of("UTC+2")))
                    .description("Otrzymano " + loan.getAmount() + " z tytułu akceptacji pożyczki.")
                    .build();
            userRepository.save(user);
            loanRepository.save(loan);
            transactionRepository.save(transaction);
            return ResponseEntity.ok().build();
        }else if(Objects.equals(decision, "REJECT")){
            loan.setStatus(LoanStatus.REJECTED);
            User user = loan.getUser();
            user.setHasLoan(false);
            userRepository.save(user);
            loanRepository.save(loan);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("Wrong decision type or loan not found.");
    }
}
