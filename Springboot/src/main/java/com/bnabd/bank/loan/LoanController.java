package com.bnabd.bank.loan;

import com.bnabd.bank.auth.AuthenticationService;
import com.bnabd.bank.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService service;


    private final AuthenticationService authService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public LoanResponse getUserLoan(@AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = authService.getUserIdFromEmail(userDetails.getUsername());
        return service.getUserLoanDetails(userId);
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/application")
    public ResponseEntity<?> applyForLoan(@RequestHeader("Authorization") String token, @RequestBody LoanRequest request){
        Optional<User> user = authService.findUserByToken(token);
        if(user.isEmpty())
            return ResponseEntity.badRequest().body("User wasn't found or access token is invalid.");
        return service.applyForLoan(user.get(), request);
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/payRate")
    public ResponseEntity<?> payRate(@RequestHeader("Authorization") String token, @AuthenticationPrincipal UserDetails userDetails){
        Integer userId = authService.getUserIdFromEmail(userDetails.getUsername());
        Optional<User> user = authService.findUserByToken(token);
        if(user.isEmpty())
            return ResponseEntity.badRequest().body("User wasn't found or access token is invalid.");
        return service.payRate(user.get(), userId);
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/repayment")
    public ResponseEntity<?> repayment(@RequestHeader("Authorization") String token,
                                       @AuthenticationPrincipal UserDetails userDetails, @RequestParam BigDecimal amount){
        Integer userId = authService.getUserIdFromEmail(userDetails.getUsername());
        Optional<User> user = authService.findUserByToken(token);
        if(user.isEmpty())
            return ResponseEntity.badRequest().body("User wasn't found or access token is invalid.");
        return service.repayment(user.get(), userId, amount);
    }
}
