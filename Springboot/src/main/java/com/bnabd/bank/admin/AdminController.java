package com.bnabd.bank.admin;

import com.bnabd.bank.loan.LoanResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService service;
    @GetMapping("/loans")
    @PreAuthorize("hasAuthority('admin:read')")
    public List<LoanResponseWrapper> findPendingLoans(){
        return service.findPendingLoans();
    }
    @PostMapping("/loans")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> changeLoanStatus(@RequestParam String decision, @RequestParam Integer loanId){
        System.out.println(decision);
        System.out.println(loanId);
        return service.changeLoanStatus(decision, loanId);
    }
}
