package org.example.customrbacjavademo.apps.auth.infra.persistence;

import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {
  @EntityGraph(attributePaths = "user")
  Optional<RefreshTokenJpaEntity> findWithUserByToken(final String token);

  @Modifying
  void deleteByUser(final UserJpaEntity user);
}
