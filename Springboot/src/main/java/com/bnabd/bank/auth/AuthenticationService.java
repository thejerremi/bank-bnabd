package com.bnabd.bank.auth;

import com.bnabd.bank.config.JwtService;
import com.bnabd.bank.token.Token;
import com.bnabd.bank.token.TokenRepository;
import com.bnabd.bank.token.TokenType;
import com.bnabd.bank.user.Role;
import com.bnabd.bank.user.User;
import com.bnabd.bank.user.UserDTO;
import com.bnabd.bank.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@ControllerAdvice
class GlobalExceptionHandler {

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
  }
}
@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request) {
    if (repository.findByEmail(request.getEmail()).isPresent()) {
      throw new IllegalStateException("User already exists.");
    }
    var user = User.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .pesel(request.getPesel())
        .dob(request.getDob())
        .email(request.getEmail())
        .hasLoan(false)
        .password(passwordEncoder.encode(request.getPassword()))
        .balance(BigDecimal.valueOf(300))
        .accountNumber(new java.util.Random().ints(26, 0, 10)
                .mapToObj(Integer::toString).collect(java.util.stream.Collectors.joining()))
        .role(Role.USER)
        .build();
    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .refreshToken(refreshToken)
            .user(convertToDTO(savedUser))
        .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .user(convertToDTO(user))
        .build();
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }
  public ResponseEntity<String> deleteUser(HttpServletRequest tokenHeader, @RequestBody AuthenticationRequest request){
    try {
      final String authHeader = tokenHeader.getHeader(HttpHeaders.AUTHORIZATION);
      final String token;
      final String userEmail;

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return new ResponseEntity<>("Invalid authorization header.", HttpStatus.BAD_REQUEST);
      }

      token = authHeader.substring(7);
      userEmail = jwtService.extractUsername(token);
      if (userEmail != null) {
        var user = this.repository.findByEmail(userEmail);
        if (user.isPresent() && jwtService.isTokenValid(token, user.get())) {
          if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            return new ResponseEntity<>("Invalid password.", HttpStatus.UNAUTHORIZED);
          }else{
            repository.delete(user.get());
            return ResponseEntity.ok("Deleted.");
          }

        } else {
          return new ResponseEntity<>("Couldn't authorize user.", HttpStatus.UNAUTHORIZED);
        }
      }
    }catch (MalformedJwtException e) {
      return new ResponseEntity<>("Invalid JWT token.", HttpStatus.UNAUTHORIZED);
    }catch (Exception e) {
      return new ResponseEntity<>("An internal error occured", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>("An internal error occured", HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public Optional<User> findUserByEmail(String email) {
    return repository.findByEmail(email);
  }

  public Integer getUserIdFromEmail(String email) {
    return repository.findByEmail(email)
            .map(User::getId)
            .orElse(null);
  }

  public Optional<User> findUserByToken(String token) {
    final String userEmail;
    User user;
    if (token == null || !token.startsWith("Bearer ")) {
      return Optional.empty();
    }
    token = token.substring(7);
    userEmail = jwtService.extractUsername(token);
    if (userEmail != null) {
      user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(token, user)) {
        return Optional.of(user);
      }
    }
    return Optional.empty();
  }
  public ResponseEntity<Object> findUserDTOByToken(HttpServletRequest request) {
    try {
      final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
      final String token;
      final String userEmail;

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Invalid authorization header"));
      }

      token = authHeader.substring(7);
      userEmail = jwtService.extractUsername(token);

      if (userEmail != null) {
        var user = this.repository.findByEmail(userEmail);
        if (user.isPresent() && jwtService.isTokenValid(token, user.get())) {
          return ResponseEntity.ok(convertToDTO(user.get()));
        } else {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Couldn't authenticate user."));
        }
      }
    }catch (MalformedJwtException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Invalid JWT token"));
    }catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An internal error occured."));
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("An internal error occured."));
  }
  public UserDTO convertToDTO(User user) {
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
  public static class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }
  }
}
