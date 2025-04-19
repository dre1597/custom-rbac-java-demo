package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.IntegrationTest;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdateRoleDto;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.RoleStatus;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.RoleTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
class UpdateRoleUseCaseIntegrationTest {
  @Autowired
  private UpdateRoleUseCase useCase;

  @Autowired
  private RoleJpaRepository repository;

  @Autowired
  private PermissionJpaRepository permissionRepository;

  @Test
  void shouldUpdateRole() {
    final var permission = PermissionTestMocks.createActiveTestPermission();
    final var permissionJpa = permissionRepository.save(PermissionMapper.entityToJpa(permission));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permissionJpa.getId()))));

    final var dto = UpdateRoleDto.of(
        "updated_name",
        "updated_description",
        RoleStatus.ACTIVE.name(),
        List.of(permissionJpa.getId().toString())
    );

    final var updatedRole = useCase.execute(role.getId().toString(), dto);

    assertNotNull(updatedRole.getId());
    assertEquals(dto.name(), updatedRole.getName());
    assertEquals(dto.description(), updatedRole.getDescription());
    assertEquals(dto.status(), updatedRole.getStatus().name());
    assertEquals(dto.permissionIds(), updatedRole.getPermissionIds().stream().map(UUID::toString).toList());
    assertNotNull(updatedRole.getCreatedAt());
    assertNotNull(updatedRole.getUpdatedAt());
  }

  @Test
  void shouldNotUpdateNonExistentRole() {
    final var id = UUID.randomUUID();
    final var dto = UpdateRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE.name(), List.of());
    final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(id.toString(), dto));
    assertEquals("Role not found", exception.getMessage());
    ;
  }

  @Test
  void shouldThrowValidationExceptionWhenIdIsNotAValidUUID() {
    final var id = "invalid_uuid";
    final var dto = UpdateRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE.name(), List.of());
    final var exception = assertThrows(ValidationException.class, () -> useCase.execute(id, dto));
    assertEquals("Invalid UUID: invalid_uuid", exception.getMessage());
  }

  @Test
  void shouldNotUpdateNoneExistentPermissions() {
    final var permission = PermissionTestMocks.createActiveTestPermission();
    final var permissionJpa = permissionRepository.save(PermissionMapper.entityToJpa(permission));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permissionJpa.getId()))));

    final var dto = UpdateRoleDto.of(
        "any_name",
        "any_description",
        RoleStatus.ACTIVE.name(),
        List.of(UUID.randomUUID().toString())
    );
    final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(role.getId().toString(), dto));
    assertEquals("Some permissions are invalid or missing. Provided: " + dto.permissionIds(), exception.getMessage());
  }

  @Test
  void shouldNotUpdateToDuplicateName() {
    final var permission = PermissionTestMocks.createActiveTestPermission();
    final var permissionJpa = permissionRepository.save(PermissionMapper.entityToJpa(permission));
    final var savedRole = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permissionJpa.getId()))));
    final var roleToUpdate = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole("other_name", List.of(permissionJpa.getId()))));

    final var dto = UpdateRoleDto.of(
        savedRole.getName(),
        "updated_description",
        RoleStatus.ACTIVE.name(),
        List.of(permissionJpa.getId().toString())
    );

    final var exception = assertThrows(AlreadyExistsException.class, () -> useCase.execute(roleToUpdate.getId().toString(), dto));
    assertEquals("Role already exists", exception.getMessage());
  }

  @Test
  void shouldUpdatePermissions() {
    final var oldPermission = PermissionTestMocks.createActiveTestPermission();
    final var oldPermissionJpa = permissionRepository.save(PermissionMapper.entityToJpa(oldPermission));
    final var newPermission = PermissionTestMocks.createActiveTestPermission(PermissionName.UPDATE.name());
    final var newPermissionJpa = permissionRepository.save(PermissionMapper.entityToJpa(newPermission));
    repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(oldPermissionJpa.getId()))));
    final var roleToUpdate = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole("other_name", List.of(oldPermissionJpa.getId()))));

    final var dto = UpdateRoleDto.of(
        roleToUpdate.getName(),
        roleToUpdate.getDescription(),
        roleToUpdate.getStatus(),
        List.of(newPermissionJpa.getId().toString())
    );

    final var updatedRole = useCase.execute(roleToUpdate.getId().toString(), dto);
    assertEquals(roleToUpdate.getId(), updatedRole.getId());
    assertEquals(roleToUpdate.getName(), updatedRole.getName());
    assertEquals(roleToUpdate.getDescription(), updatedRole.getDescription());
    assertEquals(roleToUpdate.getStatus(), updatedRole.getStatus().name());
    assertEquals(dto.permissionIds(), updatedRole.getPermissionIds().stream().map(UUID::toString).toList());
  }

  @ParameterizedTest
  @CsvSource({
      "null, any_description, ACTIVE, name is required",
      "'', any_description, ACTIVE, name is required",
      "any_name, null, ACTIVE, description is required",
      "any_name, '', ACTIVE, description is required",
      "any_name, any_description, null, status is required",
      "null, null, null, 'name is required, description is required, status is required'",
  })
  void shouldNotUpdateRoleWithInvalidInput(
      final String name,
      final String description,
      final String status,
      final String expectedMessage
  ) {
    final var permission = PermissionTestMocks.createActiveTestPermission();
    final var permissionJpa = permissionRepository.save(PermissionMapper.entityToJpa(permission));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permissionJpa.getId()))));

    final var actualName = "null".equals(name) ? null : name;
    final var actualDescription = "null".equals(description) ? null : description;
    final var actualStatus = "null".equals(String.valueOf(status)) ? null : status;
    final var dto = UpdateRoleDto.of(actualName, actualDescription, actualStatus, List.of(permissionJpa.getId().toString()));

    final var exception = assertThrows(
        ValidationException.class,
        () -> useCase.execute(role.getId().toString(), dto)
    );

    assertEquals(expectedMessage, exception.getMessage());
  }
}
