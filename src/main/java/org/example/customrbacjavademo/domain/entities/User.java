package org.example.customrbacjavademo.domain.entities;

import org.example.customrbacjavademo.domain.dto.NewUserDto;
import org.example.customrbacjavademo.domain.dto.UpdateUserDto;
import org.example.customrbacjavademo.domain.services.PasswordService;

import java.time.Instant;
import java.util.UUID;

public class User {
  private final UUID id = UUID.randomUUID();
  private final Instant createdAt = Instant.now();
  private String name;
  private String password;
  private UserStatus status;
  private Instant updatedAt = Instant.now();

  private User(
      final String name,
      final String password,
      final UserStatus status
  ) {
    this.name = name;
    this.password = PasswordService.encryptPassword(password);
    this.status = status;
  }

  public static User newUser(final NewUserDto dto) {
    var user = new User(dto.name(), dto.password(), dto.status());
    user.validate();
    return user;
  }

  public User update(final UpdateUserDto dto) {
    name = dto.name();
    updatedAt = Instant.now();
    this.validate();
    return this;
  }

  private void validate() {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name is required");
    }
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("password is required");
    }
  }

  public User updatePassword(final String newPassword) {
    if (newPassword == null || newPassword.isBlank()) {
      throw new IllegalArgumentException("password is required");
    }
    this.password = PasswordService.encryptPassword(newPassword);
    return this;
  }

  public User activate() {
    status = UserStatus.ACTIVE;
    return this;
  }

  public User deactivate() {
    status = UserStatus.INACTIVE;
    return this;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getPassword() {
    return password;
  }

  public UserStatus getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
