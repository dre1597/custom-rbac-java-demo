package org.example.customrbacjavademo.domain.entities;

import org.example.customrbacjavademo.domain.dto.NewRoleDto;
import org.example.customrbacjavademo.domain.dto.UpdateRoleDto;
import org.example.customrbacjavademo.domain.exceptions.ValidationException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public class Role {
  private final UUID id = UUID.randomUUID();
  private final Instant createdAt = Instant.now();
  private String name;
  private String description;
  private RoleStatus status;
  private Instant updatedAt = Instant.now();

  private Role(
      final String name,
      final String description,
      final RoleStatus status
  ) {
    this.validate(name, description, status);
    this.name = name;
    this.description = description;
    this.status = status;
  }

  public static Role newRole(final NewRoleDto dto) {
    return new Role(dto.name(), dto.description(), dto.status());
  }

  public Role update(final UpdateRoleDto dto) {
    this.validate(dto.name(), dto.description(), dto.status());
    this.name = dto.name();
    this.description = dto.description();
    this.status = dto.status();
    this.updatedAt = Instant.now();
    return this;
  }

  private void validate(final String name, final String description, final RoleStatus status) {
    var errors = new ArrayList<String>();

    if (name == null || name.isBlank()) {
      errors.add("name is required");
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
}
