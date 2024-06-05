package com.bnabd.bank.admin;

import com.bnabd.bank.loan.LoanResponse;
import com.bnabd.bank.user.UserDTO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoanResponseWrapper {
    private Integer id;
    private LoanResponse loanResponse;
    private UserDTO user;

}
