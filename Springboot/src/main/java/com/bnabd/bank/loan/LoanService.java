package com.bnabd.bank.loan;

import com.bnabd.bank.transaction.Transaction;
import com.bnabd.bank.transaction.TransactionRepository;
import com.bnabd.bank.transaction.TransactionType;
import com.bnabd.bank.user.User;
import com.bnabd.bank.user.UserRepository;
import com.bnabd.bank.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final UserService userService;
    private final LoanRepository repository;
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

    public LoanResponse getUserLoanDetails(Integer userId){
        return convertToResponse(repository.findTop1ByUserIdOrderByIdDesc(userId));
    }

    public BigDecimal calculateMonthlyRate(BigDecimal principal, BigDecimal annualRate, int termInMonths) {
        // Convert annual rate to monthly rate
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        // Calculate (1 + monthlyRate) ^ termInMonths
        BigDecimal onePlusRateToPower = monthlyRate.add(BigDecimal.ONE).pow(termInMonths);

        // Calculate monthly payment using the amortization formula
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRateToPower);
        BigDecimal denominator = onePlusRateToPower.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP); // 2 decimal places
    }

    public ResponseEntity<?> applyForLoan(User user, LoanRequest request) {
        if (user.getHasLoan()) {
            return ResponseEntity.badRequest().body("User already has a loan.");
        }

        BigDecimal principal = request.getAmount();
        BigDecimal annualRate = BigDecimal.valueOf(0.05);
        int termInMonths = request.getLength();

        BigDecimal monthlyRate = calculateMonthlyRate(principal, annualRate, termInMonths);

        var loan = Loan.builder()
                .amount(request.getAmount())
                .status(LoanStatus.PENDING)
                .type(request.getType())
                .length(request.getLength())
                .monthlyRate(monthlyRate)
                .paymentLeft(monthlyRate.multiply(BigDecimal.valueOf(request.getLength())))
                .user(user)
                .build();
        user.setHasLoan(true);
        userRepository.save(user);
        repository.save(loan);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> payRate(User user, Integer userId) {
        Loan userLoan = repository.findTop1ByUserIdOrderByIdDesc(userId);
        if(userLoan.getStatus() != LoanStatus.ACCEPTED)
            return ResponseEntity.badRequest().body("This loan cannot be paid off.");
        if(user.getBalance().compareTo(userLoan.getMonthlyRate()) < 0)
            return ResponseEntity.badRequest().body("Insufficient balance to pay monthly rate.");
        if(userLoan.getMonthlyRate().compareTo(userLoan.getPaymentLeft()) > 0)
            return ResponseEntity.badRequest().body("Monthly rate is bigger than required payment left.");
        userService.subtractBalance(user, userLoan.getMonthlyRate());
        var transaction = Transaction.builder()
                .type(TransactionType.MONTHLY_RATE)
                .amount(userLoan.getMonthlyRate())
                .user(user)
                .createdAt(LocalDateTime.now(ZoneId.of("UTC+2")))
                .description("Zapłacono miesięczną ratę pożyczki w wysokości " + userLoan.getMonthlyRate() + " PLN.")
                .build();
        transactionRepository.save(transaction);
        userLoan.setPaymentLeft(userLoan.getPaymentLeft().subtract(userLoan.getMonthlyRate()));
        if(userLoan.getPaymentLeft().compareTo(BigDecimal.valueOf(0.01)) < 0){
            user.setHasLoan(false);
            userLoan.setStatus(LoanStatus.PAYED_OFF);
            userRepository.save(user);
        }
        repository.save(userLoan);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> repayment(User user, Integer userId, BigDecimal amount) {
        Loan userLoan = repository.findTop1ByUserIdOrderByIdDesc(userId);
        if(userLoan.getStatus() != LoanStatus.ACCEPTED)
            return ResponseEntity.badRequest().body("This loan cannot be paid off.");
        if(user.getBalance().compareTo(amount) < 0)
            return ResponseEntity.badRequest().body("Insufficient balance to pay this amount of loan repayment.");
        if(amount.compareTo(userLoan.getPaymentLeft()) > 0)
            return ResponseEntity.badRequest().body("This loan repayment is bigger than required payment left");
        userService.subtractBalance(user, amount);
        var transaction = Transaction.builder()
                .type(TransactionType.LOAN_PAYMENT)
                .amount(amount)
                .user(user)
                .createdAt(LocalDateTime.now(ZoneId.of("UTC+2")))
                .description("Spłacono " + amount + " PLN w ramach pożyczki.")
                .build();
        transactionRepository.save(transaction);
        userLoan.setPaymentLeft(userLoan.getPaymentLeft().subtract(amount));
        if(userLoan.getPaymentLeft().compareTo(BigDecimal.valueOf(0.01)) < 0){
            user.setHasLoan(false);
            userLoan.setStatus(LoanStatus.PAYED_OFF);
            userRepository.save(user);
        }
        repository.save(userLoan);
        return ResponseEntity.ok().build();
    }
}
