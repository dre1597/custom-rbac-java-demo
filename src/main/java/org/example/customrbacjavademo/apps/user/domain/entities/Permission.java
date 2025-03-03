package org.example.customrbacjavademo.apps.user.domain.entities;

import org.example.customrbacjavademo.apps.user.domain.dto.NewPermissionDto;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdatePermissionDto;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionStatus;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;

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
      final PermissionName name,
      final PermissionScope scope,
      final String description,
      final PermissionStatus status
  ) {
    this.validate(name, scope, description, status);
    this.name = name;
    this.scope = scope;
    this.description = description;
    this.status = status;
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
    this.name = dto.name();
    this.scope = dto.scope();
    this.description = dto.description();
    this.status = dto.status();
    this.updatedAt = Instant.now();
    return this;
  }

  private void validate(final PermissionName name, final PermissionScope scope, final String description, final PermissionStatus status) {
    var errors = new ArrayList<String>();

    if (name == null) {
      errors.add("name is required");
    }
    if (scope == null) {
      errors.add("scope is required");
    }
    if (description == null || description.isBlank()) {
      errors.add("description is required");
    }
    if (status == null) {
      errors.add("status is required");
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
