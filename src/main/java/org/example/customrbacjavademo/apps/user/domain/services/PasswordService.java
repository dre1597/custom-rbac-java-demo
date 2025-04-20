package org.example.customrbacjavademo.apps.user.domain.services;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordService {
  private PasswordService() {
  }

  public static String encryptPassword(final String rawPassword) {
    return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
  }

  public static boolean matches(final String rawPassword, final String hashedPassword) {
    return BCrypt.checkpw(rawPassword, hashedPassword);
  }
}
