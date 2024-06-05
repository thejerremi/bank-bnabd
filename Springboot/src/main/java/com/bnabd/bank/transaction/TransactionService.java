package com.bnabd.bank.transaction;

import com.bnabd.bank.user.User;
import com.bnabd.bank.user.UserRepository;
import com.bnabd.bank.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.bnabd.bank.transaction.TransactionType.*;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;
    private final UserRepository userRepository;
    private final UserService userService;

    public List<TransactionResponse> getLast5Transactions(Integer userId) {
        return repository.findTop5ByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Page<TransactionResponse> getTransactionsWithPagination(Integer userId, int page, int size) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .map(this::convertToResponse);
    }

    private TransactionResponse convertToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .createdAt(transaction.getCreatedAt())
                .recipient(transaction.getRecipient())
                .description(transaction.getDescription())
                .build();
    }

    public void deposit(Transaction request, User user){
        var deposit = Transaction.builder()
                .type(DEPOSIT)
                .amount(request.getAmount())
                .user(user)
                .createdAt(LocalDateTime.now(ZoneId.of("UTC+2")))
                .description("Wpłacono " + request.getAmount() + " złotych.")
                .build();
        userService.addBalance(user, request.getAmount());
        repository.save(deposit);
    }

    public void atmDeposit(Transaction request, User user){
        var deposit = Transaction.builder()
                .type(ATM_DEPOSIT)
                .amount(request.getAmount())
                .user(user)
                .createdAt(LocalDateTime.now(ZoneId.of("UTC+2")))
                .description("Wpłacono " + request.getAmount() + " złotych we wpłatomacie.")
                .build();
        userService.addBalance(user, request.getAmount());
        repository.save(deposit);
    }

    public ResponseEntity<?> transfer(User user, TransferRequest request) {
        if(user.getBalance().compareTo(request.getAmount()) < 0){
            return ResponseEntity.badRequest().body("Insufficient balance.");
        }
        if(request.getAccountNumberDest().length() != 26){
            return ResponseEntity.badRequest().body("Incorrect acount number.");
        }
        Optional<User> userDestination = userRepository.findByAccountNumber(request.getAccountNumberDest());
        userDestination.ifPresent(value -> userService.addBalance(value, request.getAmount()));
        userService.subtractBalance(user, request.getAmount());

        String recipientDescription = userDestination
                .map(dest -> dest.getFirstname() + " " + dest.getLastname() + ", numer konta: " + dest.getAccountNumber())
                .orElse("numer konta: " + request.getAccountNumberDest());

        var transferSent = Transaction.builder()
                .type(TransactionType.TRANSFER)
                .amount(request.getAmount())
                .user(user)
                .createdAt(LocalDateTime.now(ZoneId.of("UTC+2")))
                .recipient(recipientDescription)
                .description("Przelano " + request.getAmount() + " złotych na konto: " + request.getAccountNumberDest())
                .build();

        if(userDestination.isPresent()){
            var transferReceive = Transaction.builder()
                    .type(TransactionType.USER_TRANSFER)
                    .amount(request.getAmount())
                    .user(userDestination.get())
                    .createdAt(LocalDateTime.now(ZoneId.of("UTC+2")))
                    .description("Otrzymano " + request.getAmount() + " złotych od "
                            + user.getFirstname() + " " + user.getLastname() + ", numer konta:" + user.getAccountNumber())
                    .build();
            repository.save(transferReceive);
        }
        repository.save(transferSent);
        return ResponseEntity.ok().build();
    }
}