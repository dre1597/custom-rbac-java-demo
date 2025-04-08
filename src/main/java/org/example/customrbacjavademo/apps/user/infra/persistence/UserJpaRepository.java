package org.example.customrbacjavademo.apps.user.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
  boolean existsByName(final String name);

  Optional<UserJpaEntity> findWithRoleById(final UUID id);
}
