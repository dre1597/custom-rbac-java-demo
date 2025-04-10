package org.example.customrbacjavademo.apps.user.domain.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class PasswordService {
  private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

  private PasswordService() {
  }

  public static String encryptPassword(final String rawPassword) {
    return encoder.encode(rawPassword);
  }

  public static boolean matches(final String rawPassword, final String encodedPassword) {
    return encoder.matches(rawPassword, encodedPassword);
  }
}
