package org.example.customrbacjavademo.domain.entities;

import org.example.customrbacjavademo.domain.dto.NewUserDto;

import java.time.Instant;
import java.util.UUID;

public class User {
  private UUID id = UUID.randomUUID();
  private String name;
  private String password;
  private UserStatus status = UserStatus.ACTIVE;
  private Instant createdAt = Instant.now();
  private Instant updatedAt = Instant.now();

  private User(
      final String name,
      final String password,
      final UserStatus status
  ) {
    this.name = name;
    this.password = password;
  }

  private User(
      final UUID id,
      final String name,
      final String password,
      final UserStatus status,
      final Instant createdAt,
      final Instant updatedAt
  ) {
    this.id = id;
    this.name = name;
    this.password = password;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static User newUser(final NewUserDto dto) {
    var user = new User(dto.name(), dto.password(), dto.status());
    user.validate();
    return user;
  }

  private void validate() {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name is required");
    }
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("password is required");
    }
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
