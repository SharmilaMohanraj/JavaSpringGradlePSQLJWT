package com.example.app.config;

import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Provides an administrator only for the in-memory local verification profile. */
@Component
@Profile("local")
public class LocalProfileAdminSeeder implements ApplicationRunner {
  private final UserRepository users;
  private final PasswordEncoder passwordEncoder;

  public LocalProfileAdminSeeder(UserRepository users, PasswordEncoder passwordEncoder) {
    this.users = users;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(ApplicationArguments arguments) {
    if (users.findByEmail("admin@example.com").isEmpty()) {
      User admin = new User();
      admin.setName("Local Administrator");
      admin.setEmail("admin@example.com");
      admin.setPassword(passwordEncoder.encode("password"));
      admin.setRole("admin");
      users.save(admin);
    }
  }
}
