package com.example.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
  private final Key key;

  public JwtUtil(@Value("${JWT_SECRET}") String secret) {
    if (secret == null || secret.length() < 32)
      throw new IllegalStateException("JWT_SECRET must be at least 32 characters");
    key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(String email, String role) {
    return Jwts.builder()
        .setSubject(email)
        .claim("role", role)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 1800000))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public Claims claims(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }
}
