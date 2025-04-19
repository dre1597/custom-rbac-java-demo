package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.IntegrationTest;
import org.example.customrbacjavademo.apps.user.domain.dto.NewRoleDto;
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
class CreateRoleUseCaseIntegrationTest {
  @Autowired
  private CreateRoleUseCase useCase;

  @Autowired
  private RoleJpaRepository repository;

  @Autowired
  private PermissionJpaRepository permissionRepository;

  @Test
  void shouldCreateRole() {
    final var permission = PermissionTestMocks.createActiveTestPermission();
    final var permissionJpa = permissionRepository.save(PermissionMapper.entityToJpa(permission));

    final var dto = NewRoleDto.of(
        "any_name",
        "any_description",
        RoleStatus.ACTIVE.name(),
        List.of(permissionJpa.getId().toString())
    );

    final var role = useCase.execute(dto);

    assertNotNull(role.getId());
    assertEquals(dto.name(), role.getName());
    assertEquals(dto.description(), role.getDescription());
    assertEquals(dto.status(), role.getStatus().name());
    assertEquals(dto.permissionIds(), role.getPermissionIds().stream().map(UUID::toString).toList());
    assertNotNull(role.getCreatedAt());
    assertNotNull(role.getUpdatedAt());
  }

  @Test
  void shouldNotCreateRoleIfNameAlreadyExists() {
    final var permission = PermissionTestMocks.createActiveTestPermission();
    final var permissionJpa = permissionRepository.save(PermissionMapper.entityToJpa(permission));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permissionJpa.getId()))));

    final var dto = NewRoleDto.of(
        role.getName(),
        "any_description",
        RoleStatus.ACTIVE.name(),
        List.of(permissionJpa.getId().toString())
    );

    var exception = assertThrows(AlreadyExistsException.class, () -> useCase.execute(dto));

    assertEquals("Role already exists", exception.getMessage());
  }

  @Test
  void shouldThrowIfSomePermissionsAreInvalid() {
    final var permission = PermissionTestMocks.createActiveTestPermission();
    final var permissionJpa = permissionRepository.save(PermissionMapper.entityToJpa(permission));
    repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permissionJpa.getId()))));

    final var dto = NewRoleDto.of(
        "updated_name",
        "updated_description",
        RoleStatus.ACTIVE.name(),
        List.of(permissionJpa.getId().toString(), UUID.randomUUID().toString())
    );

    final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(dto));

    assertEquals("Some permissions are invalid or missing. Provided: " + dto.permissionIds(), exception.getMessage());
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
  void shouldNotCreateRoleWithInvalidInput(
      final String name,
      final String description,
      final String status,
      final String expectedMessage
  ) {
    final var permission = PermissionTestMocks.createActiveTestPermission();
    final var permissionJpa = permissionRepository.save(PermissionMapper.entityToJpa(permission));

    final var actualName = "null".equals(name) ? null : name;
    final var actualDescription = "null".equals(description) ? null : description;
    final var actualStatus = "null".equals(String.valueOf(status)) ? null : status;
    final var dto = NewRoleDto.of(actualName, actualDescription, actualStatus, List.of(permissionJpa.getId().toString()));

    final var exception = assertThrows(
        ValidationException.class,
        () -> useCase.execute(dto)
    );

    assertEquals(expectedMessage, exception.getMessage());
  }
}
