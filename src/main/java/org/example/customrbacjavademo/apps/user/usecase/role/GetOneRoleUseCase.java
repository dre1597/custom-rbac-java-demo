package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.RoleDetailsResponse;
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

  public RoleDetailsResponse execute(final UUID id) {
    return repository.findWithPermissionsById(id)
        .map(RoleMapper::jpaToDetailsResponse)
        .orElseThrow(() -> new NotFoundException("Role not found"));
  }
}
