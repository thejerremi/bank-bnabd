package com.bnabd.bank.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;


@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
    }
    public void updateDetails(UserDTO request, Principal connectedUser){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if(request.getFirstname() != null)
            user.setFirstname(request.getFirstname());
        if(request.getLastname() != null)
            user.setLastname(request.getLastname());
        if(request.getDob() != null)
            user.setDob(request.getDob());
        repository.save(user);
    }
    public void addBalance(User user, BigDecimal amount){
        user.setBalance(user.getBalance().add(amount));
        repository.save(user);
    }
    public void subtractBalance(User user, BigDecimal amount){
        user.setBalance(user.getBalance().subtract(amount));
        repository.save(user);
    }
}
