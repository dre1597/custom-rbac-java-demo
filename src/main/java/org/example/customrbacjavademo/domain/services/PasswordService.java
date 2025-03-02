package org.example.customrbacjavademo.domain.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordService {
  private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

  public static String encryptPassword(final String rawPassword) {
    return encoder.encode(rawPassword);
  }

  public static boolean matches(final String rawPassword, final String encodedPassword) {
    return encoder.matches(rawPassword, encodedPassword);
  }
}
