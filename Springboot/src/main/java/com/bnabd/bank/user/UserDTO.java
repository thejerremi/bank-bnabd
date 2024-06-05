package com.bnabd.bank.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String firstname;
    private String lastname;
    private String email;
    private String pesel;
    private LocalDate dob;
    private BigDecimal balance;
    private String accountNumber;
    private Boolean hasLoan;
    private Role role;

}
