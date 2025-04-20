package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.apps.user.domain.dto.UpdateRoleDto;
import org.example.customrbacjavademo.apps.user.domain.entities.Role;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.example.customrbacjavademo.common.domain.helpers.UUIDValidator;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateRoleUseCase {
  private final RoleJpaRepository repository;
  private final PermissionJpaRepository permissionRepository;

  public UpdateRoleUseCase(final RoleJpaRepository repository, final PermissionJpaRepository permissionRepository) {
    this.repository = Objects.requireNonNull(repository);
    this.permissionRepository = Objects.requireNonNull(permissionRepository);
  }

  public Role execute(final String id, final UpdateRoleDto dto) {
    final var idAsUUID = UUIDValidator.parseOrThrow(id);
    final var roleOnDatabase = repository.findWithPermissionsById(idAsUUID).orElseThrow(() -> new NotFoundException("Role not found"));
    final var role = RoleMapper.jpaToEntity(roleOnDatabase);

    this.ensurePermissionsExist(dto);
    this.ensureRoleIsUnique(role, dto);

    role.update(dto);
    repository.save(RoleMapper.entityToJpa(role));
    return role;
  }

  private void ensurePermissionsExist(final UpdateRoleDto dto) {
    final var permissionIdsAsUUID = UUIDValidator.parseOrThrow(dto.permissionIds());
    final var foundPermissionsCount = permissionRepository.countByIdIn(permissionIdsAsUUID);

    if (foundPermissionsCount != dto.permissionIds().size()) {
      throw new NotFoundException(
          "Some permissions are invalid or missing. Provided: " + dto.permissionIds()
      );
    }
  }

  private void ensureRoleIsUnique(final Role role, final UpdateRoleDto dto) {
    final var isChangingName = dto.name() != null && !dto.name().equals(role.getName());

    if (isChangingName) {
      final var exists = repository.existsByName(dto.name());

      if (exists) {
        throw new AlreadyExistsException("Role already exists");
      }
    }
  }
}
