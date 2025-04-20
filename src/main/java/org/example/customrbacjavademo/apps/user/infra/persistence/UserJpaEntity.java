package org.example.customrbacjavademo.apps.user.infra.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "users")
@Table
public class UserJpaEntity {
  @Id
  private UUID id = UUID.randomUUID();

  @Column
  private String name;

  @Column
  private String password;

  @Column
  private String status;

  @Column
  private Instant createdAt;

  @Column
  private Instant updatedAt;

  @ManyToOne
  @JoinColumn(name = "role_id")
  private RoleJpaEntity role;

  public UserJpaEntity() {
  }

  public UserJpaEntity(
      final UUID id,
      final String name,
      final String password,
      final String status,
      final Instant createdAt,
      final Instant updatedAt,
      final RoleJpaEntity role
  ) {
    this.id = id;
    this.name = name;
    this.password = password;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.role = role;
  }

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public Instant getCreatedAt() {
    return this.createdAt;
  }

  public void setCreatedAt(final Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
    return this.updatedAt;
  }

  public void setUpdatedAt(final Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public RoleJpaEntity getRole() {
    return this.role;
  }

  public void setRole(final RoleJpaEntity role) {
    this.role = role;
  }
}
