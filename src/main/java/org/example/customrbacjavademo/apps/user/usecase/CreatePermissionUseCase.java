package org.example.customrbacjavademo.apps.user.usecase;

import org.example.customrbacjavademo.apps.user.domain.dto.NewPermissionDto;
import org.example.customrbacjavademo.apps.user.domain.entities.Permission;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.mappers.PermissionMapper;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CreatePermissionUseCase {
  private final PermissionJpaRepository repository;

  public CreatePermissionUseCase(final PermissionJpaRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  public void execute(final NewPermissionDto dto) {
    var exists = repository.existsByNameAndScope(dto.name().toString(), dto.scope().toString());

    if (exists) {
      throw new AlreadyExistsException("Permission already exists");
    }
    
    var entity = Permission.newPermission(dto);
    repository.save(PermissionMapper.entityToJpa(entity));
  }
}
