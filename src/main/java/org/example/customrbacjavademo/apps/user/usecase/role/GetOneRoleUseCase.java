package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.RoleDetailsResponse;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.example.customrbacjavademo.common.domain.helpers.UUIDValidator;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetOneRoleUseCase {
  private final RoleJpaRepository repository;

  public GetOneRoleUseCase(final RoleJpaRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  public RoleDetailsResponse execute(final String id) {
    final var idAsUUID = UUIDValidator.parseOrThrow(id);
    
    return repository.findWithPermissionsById(idAsUUID)
        .map(RoleMapper::jpaToDetailsResponse)
        .orElseThrow(() -> new NotFoundException("Role not found"));
  }
}
