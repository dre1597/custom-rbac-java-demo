package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.apps.user.domain.dto.NewRoleDto;
import org.example.customrbacjavademo.apps.user.domain.entities.Role;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.example.customrbacjavademo.common.domain.exceptions.InvalidReferenceException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CreateRoleUseCase {
  private final RoleJpaRepository repository;
  private final PermissionJpaRepository permissionJpaRepository;

  public CreateRoleUseCase(final RoleJpaRepository repository, final PermissionJpaRepository permissionJpaRepository) {
    this.repository = Objects.requireNonNull(repository);
    this.permissionJpaRepository = Objects.requireNonNull(permissionJpaRepository);
  }

  public void execute(final NewRoleDto dto) {
    final var exists = repository.existsByName(dto.name());

    if (exists) {
      throw new AlreadyExistsException("Role already exists");
    }

    final var foundPermissions = permissionJpaRepository.countByIdIn(dto.permissionIds());

    if (foundPermissions != dto.permissionIds().size()) {
      throw new InvalidReferenceException(
          "Some permissions are invalid or missing. Provided: " + dto.permissionIds()
      );
    }

    final var entity = Role.newRole(dto);
    repository.save(RoleMapper.entityToJpa(entity));
  }
}
