package com.example.app.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public final class AuthDtos {
  private AuthDtos() {}

  public static class RegisterRequest {
    @NotBlank public String name;
    @Email public String email;

    @Size(min = 8)
    public String password;
  }

  public static class LoginRequest {
    @Email public String email;
    @NotBlank public String password;
  }

  public static class JwtResponse {
    public String token;
    public String role;

    public JwtResponse(String token, String role) {
      this.token = token;
      this.role = role;
    }
  }
}
