package org.example.customrbacjavademo.apps.user.usecase.permission;

import org.example.customrbacjavademo.apps.user.domain.dto.UpdatePermissionDto;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class UpdatePermissionUseCase {
  private final PermissionJpaRepository repository;

  public UpdatePermissionUseCase(final PermissionJpaRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  public void execute(final UUID id, final UpdatePermissionDto dto) {
    final var permissionOnDatabase = repository.findById(id).orElseThrow(() -> new NotFoundException("Permission not found"));
    final var permission = PermissionMapper.jpaToEntity(permissionOnDatabase);


    final var isChangingName = !dto.name().equals(permission.getName());
    final var isChangingScope = !dto.scope().equals(permission.getScope());

    if (isChangingName || isChangingScope) {
      final var exists = repository.existsByNameAndScope(dto.name().toString(), dto.scope().toString());

      if (exists) {
        throw new AlreadyExistsException("Permission already exists");
      }
    }

    permission.update(dto);
    repository.save(PermissionMapper.entityToJpa(permission));
  }
}
