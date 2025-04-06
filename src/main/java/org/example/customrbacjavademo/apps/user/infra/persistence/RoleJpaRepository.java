package org.example.customrbacjavademo.apps.user.infra.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, UUID> {
  Page<RoleJpaEntity> findAll(final Specification<RoleJpaRepository> whereClause, final Pageable page);

  boolean existsByName(final String name);

  @EntityGraph(attributePaths = "permissions")
  Optional<RoleJpaEntity> findWithPermissionsById(final UUID id);
}
