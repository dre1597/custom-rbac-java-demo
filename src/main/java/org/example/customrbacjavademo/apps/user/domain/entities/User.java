package org.example.customrbacjavademo.apps.user.domain.entities;

import org.example.customrbacjavademo.apps.user.domain.dto.NewUserDto;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdateUserDto;
import org.example.customrbacjavademo.apps.user.domain.enums.UserStatus;
import org.example.customrbacjavademo.apps.user.domain.services.PasswordService;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.example.customrbacjavademo.common.domain.helpers.EnumUtils;
import org.example.customrbacjavademo.common.domain.helpers.EnumValidator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
  private UUID id = UUID.randomUUID();
  private String name;
  private String password;
  private UserStatus status;
  private Instant createdAt = Instant.now();
  private Instant updatedAt = Instant.now();
  private UUID roleId;

  private User(
      final UUID id,
      final String name,
      final String password,
      final UserStatus status,
      final Instant createdAt,
      final Instant updatedAt,
      final UUID roleId
  ) {
    this.id = id;
    this.name = name;
    this.password = password;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.roleId = roleId;
  }

  private User(
      final String name,
      final String password,
      final String status,
      final String roleId
  ) {
    this.validate(
        name,
        password,
        status,
        roleId
    );

    this.name = name.trim();
    this.password = PasswordService.encryptPassword(password.trim());
    this.status = UserStatus.valueOf(status);
    this.roleId = UUID.fromString(roleId);
  }

  public static User with(
      final UUID id,
      final String name,
      final String password,
      final UserStatus status,
      final Instant createdAt,
      final Instant updatedAt,
      final UUID roleId
  ) {
    return new User(
        id,
        name,
        password,
        status,
        createdAt,
        updatedAt,
        roleId
    );
  }

  public static User newUser(final NewUserDto dto) {
    return new User(
        dto.name(),
        dto.password(),
        dto.status(),
        dto.roleId()
    );
  }

  public User update(final UpdateUserDto dto) {
    this.validate(
        dto.name(),
        this.password,
        dto.status(),
        dto.roleId()
    );

    this.name = dto.name().trim();
    this.status = UserStatus.valueOf(dto.status());
    this.roleId = UUID.fromString(dto.roleId());
    this.updatedAt = Instant.now();

    return this;
  }

  private void validate(
      final String name,
      final String password,
      final String status,
      final String roleId
  ) {
    final var errors = new ArrayList<String>();

    if (name == null || name.isBlank()) {
      errors.add("name is required");
    }

    if (password == null || password.isBlank()) {
      errors.add("password is required");
    }

    if (status == null) {
      errors.add("status is required");
    } else if (EnumValidator.isInvalidEnum(status, UserStatus.class)) {
      errors.add("status must be one of " + EnumUtils.enumValuesAsString(UserStatus.class));
    }

    if (roleId == null) {
      errors.add("roleId is required");
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  public User updatePassword(final String newPassword) {
    if (newPassword == null || newPassword.isBlank()) {
      throw new ValidationException(List.of("new password is required"));
    }
    this.password = PasswordService.encryptPassword(newPassword);
    return this;
  }

  public UUID getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getPassword() {
    return this.password;
  }

  public UserStatus getStatus() {
    return this.status;
  }

  public UUID getRoleId() {
    return this.roleId;
  }

  public Instant getCreatedAt() {
    return this.createdAt;
  }

  public Instant getUpdatedAt() {
    return this.updatedAt;
  }
}
