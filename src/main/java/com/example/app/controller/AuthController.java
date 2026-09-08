package com.example.app.controller;

import com.example.app.dto.AuthDtos.JwtResponse;
import com.example.app.dto.AuthDtos.LoginRequest;
import com.example.app.dto.AuthDtos.RegisterRequest;
import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import com.example.app.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
public class AuthController {
  private final UserRepository users;
  private final PasswordEncoder encoder;
  private final JwtUtil jwt;

  public AuthController(UserRepository users, PasswordEncoder encoder, JwtUtil jwt) {
    this.users = users;
    this.encoder = encoder;
    this.jwt = jwt;
  }

  @PostMapping("/register")
  @Operation(summary = "Register an employee account")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    if (users.findByEmail(request.email).isPresent())
      return ResponseEntity.status(409).body("Email already registered");
    User user = new User();
    user.setName(request.name);
    user.setEmail(request.email);
    user.setPassword(encoder.encode(request.password));
    user.setRole("employee");
    User saved = users.save(user);
    return ResponseEntity.ok(
        new JwtResponse(jwt.generateToken(saved.getEmail(), saved.getRole()), saved.getRole()));
  }

  @PostMapping("/login")
  @Operation(summary = "Log in and obtain a JWT")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
    User user = users.findByEmail(request.email).orElse(null);
    if (user == null || !encoder.matches(request.password, user.getPassword()))
      return ResponseEntity.status(401).body("Invalid credentials");
    return ResponseEntity.ok(
        new JwtResponse(jwt.generateToken(user.getEmail(), user.getRole()), user.getRole()));
  }
}
