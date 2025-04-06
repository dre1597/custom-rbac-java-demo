package org.example.customrbacjavademo.apps.user.infra.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PermissionJpaRepository extends JpaRepository<PermissionJpaEntity, UUID> {
  Page<PermissionJpaEntity> findAll(final Specification<PermissionJpaRepository> whereClause, final Pageable page);

  boolean existsByNameAndScope(final String name, final String scope);

  long countByIdIn(List<UUID> ids);
}
