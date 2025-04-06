package org.example.customrbacjavademo.apps.user.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, UUID> {
  boolean existsByName(final String name);
}
