package org.example.customrbacjavademo.apps.user.infra.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "roles")
@Table
public class RoleJpaEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column
  private String name;

  @Column
  private String description;

  @Column
  private String status;

  @Column
  private Instant createdAt = Instant.now();

  @Column
  private Instant updatedAt = Instant.now();

  @ManyToMany
  @JoinTable(
      name = "role_permissions",
      joinColumns = @JoinColumn(name = "role_id"),
      inverseJoinColumns = @JoinColumn(name = "permission_id")
  )
  private List<PermissionJpaEntity> permissions = new ArrayList<>();

  public RoleJpaEntity() {
  }

  public RoleJpaEntity(
      final UUID id,
      final String name,
      final String description,
      final String status,
      final Instant createdAt,
      final Instant updatedAt,
      final List<PermissionJpaEntity> permissions) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.permissions = permissions;
  }

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(final Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(final Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public List<PermissionJpaEntity> getPermissions() {
    return permissions;
  }

  public void setPermissions(final List<PermissionJpaEntity> permissions) {
    this.permissions = permissions;
  }
}
