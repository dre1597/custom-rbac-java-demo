package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.apps.user.domain.dto.UpdateRoleDto;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.example.customrbacjavademo.common.domain.exceptions.InvalidReferenceException;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class UpdateRoleUseCase {
  private final RoleJpaRepository repository;
  private final PermissionJpaRepository permissionRepository;


  public UpdateRoleUseCase(final RoleJpaRepository repository, final PermissionJpaRepository permissionRepository) {
    this.repository = Objects.requireNonNull(repository);
    this.permissionRepository = Objects.requireNonNull(permissionRepository);
  }

  public void execute(final UUID id, final UpdateRoleDto dto) {
    final var roleOnDatabase = repository.findById(id).orElseThrow(() -> new NotFoundException("Role not found"));

    final var foundPermissionsCount = permissionRepository.countByIdIn(dto.permissionIds());

    if (foundPermissionsCount != dto.permissionIds().size()) {
      throw new InvalidReferenceException(
          "Some permissions are invalid or missing. Provided: " + dto.permissionIds()
      );
    }

    final var role = RoleMapper.jpaToEntity(roleOnDatabase);

    final var isChangingName = !dto.name().equals(role.getName());

    if (isChangingName) {
      final var exists = repository.existsByName(dto.name());

      if (exists) {
        throw new AlreadyExistsException("Role already exists");
      }
    }

    role.update(dto);
    repository.save(RoleMapper.entityToJpa(role));
  }
}
