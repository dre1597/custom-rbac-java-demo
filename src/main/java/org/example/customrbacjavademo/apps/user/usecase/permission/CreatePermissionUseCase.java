package org.example.customrbacjavademo.apps.user.usecase.permission;

import org.example.customrbacjavademo.apps.user.domain.dto.NewPermissionDto;
import org.example.customrbacjavademo.apps.user.domain.entities.Permission;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CreatePermissionUseCase {
  private final PermissionJpaRepository repository;

  public CreatePermissionUseCase(final PermissionJpaRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  public Permission execute(final NewPermissionDto dto) {
    final var exists = repository.existsByNameAndScope(dto.name(), dto.scope());

    if (exists) {
      throw new AlreadyExistsException("Permission already exists");
    }

    final var entity = Permission.newPermission(dto);
    final var savedPermission = repository.save(PermissionMapper.entityToJpa(entity));
    
    return PermissionMapper.jpaToEntity(savedPermission);
  }
}
