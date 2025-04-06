package org.example.customrbacjavademo.apps.user.domain.entities;

import org.example.customrbacjavademo.apps.user.domain.dto.NewRoleDto;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdateRoleDto;
import org.example.customrbacjavademo.apps.user.domain.enums.RoleStatus;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Role {
  private UUID id = UUID.randomUUID();
  private String name;
  private String description;
  private RoleStatus status;
  private Instant createdAt = Instant.now();
  private Instant updatedAt = Instant.now();
  private List<UUID> permissionIds;

  private Role(
      final String name,
      final String description,
      final RoleStatus status,
      final List<UUID> permissionIds
  ) {
    this.validate(name, description, status, permissionIds);
    this.name = name;
    this.description = description;
    this.status = status;
    this.permissionIds = permissionIds;
  }

  private Role(
      final UUID id,
      final String name,
      final String description,
      final RoleStatus status,
      final Instant createdAt,
      final Instant updatedAt,
      final List<UUID> permissionIds
  ) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.permissionIds = permissionIds;
  }


  public static Role with(
      final UUID id,
      final String name,
      final String description,
      final RoleStatus status,
      final Instant createdAt,
      final Instant updatedAt,
      final List<UUID> permissionIds
  ) {
    return new Role(
        id,
        name,
        description,
        status,
        createdAt,
        updatedAt,
        permissionIds
    );
  }

  public static Role newRole(final NewRoleDto dto) {
    return new Role(dto.name(), dto.description(), dto.status(), dto.permissionIds());
  }

  public Role update(final UpdateRoleDto dto) {
    this.validate(dto.name(), dto.description(), dto.status(), dto.permissionIds());
    this.name = dto.name();
    this.description = dto.description();
    this.status = dto.status();
    this.permissionIds = dto.permissionIds();
    this.updatedAt = Instant.now();
    return this;
  }

  private void validate(
      final String name,
      final String description,
      final RoleStatus status,
      final List<UUID> permissionIds
  ) {
    final var errors = new ArrayList<String>();

    if (name == null || name.isBlank()) {
      errors.add("name is required");
    }

    if (description == null || description.isBlank()) {
      errors.add("description is required");
    }

    if (status == null) {
      errors.add("status is required");
    }

    if (permissionIds == null || permissionIds.isEmpty()) {
      errors.add("at least one permissionId is required");
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

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public RoleStatus getStatus() {
    return status;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public List<UUID> getPermissionIds() {
    return permissionIds;
  }
}
