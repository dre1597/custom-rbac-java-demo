package org.example.customrbacjavademo.apps.user.domain.entities;

import org.example.customrbacjavademo.apps.user.domain.dto.NewPermissionDto;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdatePermissionDto;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionStatus;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.example.customrbacjavademo.common.domain.helpers.EnumValidator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public class Permission {
  private UUID id = UUID.randomUUID();
  private PermissionName name;
  private PermissionScope scope;
  private String description;
  private PermissionStatus status;
  private Instant createdAt = Instant.now();
  private Instant updatedAt = Instant.now();

  private Permission(
      final UUID id,
      final PermissionName name,
      final PermissionScope scope,
      final String description,
      final PermissionStatus status,
      final Instant createdAt,
      final Instant updatedAt
  ) {
    this.id = id;
    this.name = name;
    this.scope = scope;
    this.description = description;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  private Permission(
      final String name,
      final String scope,
      final String description,
      final String status
  ) {
    this.validate(name, scope, description, status);
    this.name = PermissionName.valueOf(name);
    this.scope = PermissionScope.valueOf(scope);
    this.description = description;
    this.status = PermissionStatus.valueOf(status);
  }

  public static Permission with(
      final UUID id,
      final PermissionName name,
      final PermissionScope scope,
      final String description,
      final PermissionStatus status,
      final Instant createdAt,
      final Instant updatedAt
  ) {
    return new Permission(
        id,
        name,
        scope,
        description,
        status,
        createdAt,
        updatedAt
    );
  }

  public static Permission newPermission(final NewPermissionDto dto) {
    return new Permission(dto.name(), dto.scope(), dto.description(), dto.status());
  }

  public Permission update(final UpdatePermissionDto dto) {
    this.validate(dto.name(), dto.scope(), dto.description(), dto.status());
    this.name = PermissionName.valueOf(dto.name());
    this.scope = PermissionScope.valueOf(dto.scope());
    this.description = dto.description();
    this.status = PermissionStatus.valueOf(dto.status());
    this.updatedAt = Instant.now();
    return this;
  }

  private void validate(final String name, final String scope, final String description, final String status) {
    final var errors = new ArrayList<String>();

    if (name == null) {
      errors.add("name is required");
    } else if (EnumValidator.isInvalidEnum(name, PermissionName.class)) {
      errors.add("name must be one of " + EnumValidator.enumValuesAsString(PermissionName.class));
    }

    if (scope == null) {
      errors.add("scope is required");
    } else if (status != null && EnumValidator.isInvalidEnum(scope, PermissionScope.class)) {
      errors.add("scope must be one of " + EnumValidator.enumValuesAsString(PermissionScope.class));
    }

    if (description == null || description.isBlank()) {
      errors.add("description is required");
    }

    if (status == null) {
      errors.add("status is required");
    } else if (EnumValidator.isInvalidEnum(status, PermissionStatus.class)) {
      errors.add("status must be one of " + EnumValidator.enumValuesAsString(PermissionStatus.class));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  public UUID getId() {
    return id;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public PermissionName getName() {
    return name;
  }

  public PermissionScope getScope() {
    return scope;
  }

  public String getDescription() {
    return description;
  }

  public PermissionStatus getStatus() {
    return status;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
