package org.example.customrbacjavademo.apps.user.domain.entities;

import org.example.customrbacjavademo.apps.user.domain.dto.NewUserDto;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdateUserDto;
import org.example.customrbacjavademo.apps.user.domain.enums.UserStatus;
import org.example.customrbacjavademo.apps.user.domain.services.PasswordService;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
    this.validate(name, password, status);
    this.name = name.trim();
    this.password = PasswordService.encryptPassword(password.trim());
    this.status = status;
  }

  public static User newUser(final NewUserDto dto) {
    return new User(dto.name(), dto.password(), dto.status());
  }

  public User update(final UpdateUserDto dto) {
    this.validate(dto.name(), this.password, dto.status());
    name = dto.name().trim();
    status = dto.status();
    updatedAt = Instant.now();
    return this;
  }

  private void validate(final String name, final String password, final UserStatus status) {
    var errors = new ArrayList<String>();

    if (name == null || name.isBlank()) {
      errors.add("name is required");
    }
    if (password == null || password.isBlank()) {
      errors.add("password is required");
    }
    if (status == null) {
      errors.add("status is required");
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  public User updatePassword(final String newPassword) {
    if (newPassword == null || newPassword.isBlank()) {
      throw new ValidationException(List.of("password is required"));
    }
    this.password = PasswordService.encryptPassword(newPassword);
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
