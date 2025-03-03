package org.example.customrbacjavademo.apps.user.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PermissionJpaRepository extends JpaRepository<PermissionJpaEntity, UUID> {
  boolean existsByNameAndScope(final String name, final String scope);
}
