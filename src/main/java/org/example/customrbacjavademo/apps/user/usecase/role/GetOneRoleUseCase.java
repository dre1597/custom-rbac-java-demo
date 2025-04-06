package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.apps.user.domain.entities.Role;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class GetOneRoleUseCase {
  private final RoleJpaRepository repository;

  public GetOneRoleUseCase(final RoleJpaRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  public Role execute(final UUID id) {
    return repository.findById(id)
        .map(RoleMapper::jpaToEntity)
        .orElseThrow(() -> new NotFoundException("Role not found"));
  }
}
