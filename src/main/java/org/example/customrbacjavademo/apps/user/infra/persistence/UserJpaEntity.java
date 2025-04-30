package org.example.customrbacjavademo.apps.user.infra.persistence;

import jakarta.persistence.*;
import org.example.customrbacjavademo.apps.auth.infra.persistence.RefreshTokenJpaEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity(name = "users")
@Table
public class UserJpaEntity implements UserDetails {
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

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private RefreshTokenJpaEntity refreshToken = null;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    var authority = new SimpleGrantedAuthority("ROLE_" + role.getName());

    return List.of(authority);
  }

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

  @Override
  public String getUsername() {
    return this.name;
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

  @Override
  public String toString() {
    final var roleStr = role != null ? role.toString() : "null";

    return """
        UserJpaEntity{
            id=%s,
            name='%s',
            status='%s',
            createdAt=%s,
            updatedAt=%s,
            role=%s
        }""".formatted(
        this.id, this.name, this.status,
        this.createdAt, this.updatedAt, roleStr
    );
  }
}
