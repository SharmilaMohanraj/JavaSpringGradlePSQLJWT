package com.example.app.security;

import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository users;

  public UserDetailsServiceImpl(UserRepository users) {
    this.users = users;
  }

  public UserDetails loadUserByUsername(String email) {
    User user = users.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    return new org.springframework.security.core.userdetails.User(
        user.getEmail(),
        user.getPassword(),
        Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase())));
  }
}
