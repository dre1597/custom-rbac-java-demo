package org.example.customrbacjavademo.apps.auth.infra.persistence;

import jakarta.persistence.*;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaEntity;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "refresh_tokens")
@Table
public class RefreshTokenJpaEntity {
  @Id
  private UUID id = UUID.randomUUID();

  @Column(nullable = false, unique = true, columnDefinition = "TEXT")
  private String token;

  @Column(nullable = false)
  private Instant expiryDate;

  @Column(nullable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  @OneToOne
  @JoinColumn(name = "user_id")
  private UserJpaEntity user;

  public RefreshTokenJpaEntity() {
  }

  public RefreshTokenJpaEntity(
      final UUID id,
      final String token,
      final Instant expiryDate,
      final Instant createdAt,
      final Instant updatedAt,
      final UserJpaEntity user
  ) {
    this.id = id;
    this.token = token;
    this.expiryDate = expiryDate;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.user = user;
  }

  public UUID getId() {
    return this.id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public String getToken() {
    return this.token;
  }

  public void setToken(final String token) {
    this.token = token;
  }

  public Instant getExpiryDate() {
    return this.expiryDate;
  }

  public void setExpiryDate(final Instant expiryDate) {
    this.expiryDate = expiryDate;
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

  public UserJpaEntity getUser() {
    return this.user;
  }

  public void setUser(final UserJpaEntity user) {
    this.user = user;
  }

  @Override
  public String toString() {
    return "RefreshTokenJpaEntity{" +
        "id=" + id +
        ", token='" + token + '\'' +
        ", expiryDate=" + expiryDate +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        ", user=" + user +
        '}';
  }
}
