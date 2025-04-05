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

    permission.update(dto);

    var hasTheSameName = dto.name().equals(permission.getName());
    var hasTheSameScope = dto.scope().equals(permission.getScope());

    if (hasTheSameName && hasTheSameScope) {
      var exists = repository.existsByNameAndScope(dto.name().toString(), dto.scope().toString());

      if (exists) {
        throw new AlreadyExistsException("Permission already exists");
      }
    }
    repository.save(PermissionMapper.entityToJpa(permission));
  }
}
