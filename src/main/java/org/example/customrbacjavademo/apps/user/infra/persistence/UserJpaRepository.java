package org.example.customrbacjavademo.apps.user.infra.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
  Page<UserJpaEntity> findAll(final Specification<UserJpaRepository> whereClause, final Pageable page);

  boolean existsByName(final String name);

  Optional<UserJpaEntity> findWithRoleById(final UUID id);

  Optional<UserJpaEntity> findWithRoleByName(final String name);

  Optional<UserJpaEntity> findByName(String email);
}
