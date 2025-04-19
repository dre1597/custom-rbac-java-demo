package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.IntegrationTest;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdateUserDto;
import org.example.customrbacjavademo.apps.user.domain.enums.UserStatus;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.RoleTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.UserTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;
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
class UpdateUserUseCaseIntegrationTest {
  @Autowired
  private UpdateUserUseCase updateUserUseCase;

  @Autowired
  private UserJpaRepository repository;

  @Autowired
  private RoleJpaRepository roleRepository;

  @Autowired
  private PermissionJpaRepository permissionRepository;

  @Test
  void shouldUpdateUser() {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var dto = UpdateUserDto.of(
        "updated_name",
        UserStatus.INACTIVE.name(),
        role.getId().toString()
    );

    final var updatedUser = updateUserUseCase.execute(user.getId().toString(), dto);

    assertEquals(user.getId(), updatedUser.getId());
    assertEquals(dto.name(), updatedUser.getName());
    assertEquals(dto.status(), updatedUser.getStatus().toString());
    assertEquals(dto.roleId(), updatedUser.getRoleId().toString());
    assertNotNull(updatedUser.getCreatedAt());
    assertNotNull(updatedUser.getUpdatedAt());
  }

  @Test
  void shouldNotUpdateNonExistentUser() {
    final var dto = UpdateUserDto.of(
        "any_name",
        UserStatus.ACTIVE.name(),
        UUID.randomUUID().toString()
    );

    final var exception = assertThrows(
        NotFoundException.class,
        () -> updateUserUseCase.execute(UUID.randomUUID().toString(), dto)
    );

    assertEquals("User not found", exception.getMessage());
  }

  @Test
  void shouldNotUpdateUserWithNonExistentRole() {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var dto = UpdateUserDto.of(
        user.getName(),
        user.getStatus(),
        UUID.randomUUID().toString()
    );

    final var exception = assertThrows(
        NotFoundException.class,
        () -> updateUserUseCase.execute(user.getId().toString(), dto)
    );

    assertEquals("Role not found", exception.getMessage());
  }

  @Test
  void shouldNotUpdateUserWithInvalidRoleId() {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var dto = UpdateUserDto.of(
        user.getName(),
        user.getStatus(),
        "invalid_role_id"
    );

    final var exception = assertThrows(
        ValidationException.class,
        () -> updateUserUseCase.execute(user.getId().toString(), dto)
    );

    assertEquals("Invalid UUID: invalid_role_id", exception.getMessage());
  }

  @Test
  void shouldNotUpdateToDuplicatedName() {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));
    final var userToUpdate = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser("other_name", role.getId())));

    final var dto = UpdateUserDto.of(
        user.getName(),
        userToUpdate.getStatus(),
        role.getId().toString()
    );

    final var exception = assertThrows(
        AlreadyExistsException.class,
        () -> updateUserUseCase.execute(userToUpdate.getId().toString(), dto)
    );

    assertEquals("User already exists", exception.getMessage());
  }

  @Test
  void shouldUpdateRole() {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var oldRole = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var newRole = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole("new_role", List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(oldRole.getId())));

    final var dto = UpdateUserDto.of(
        user.getName(),
        user.getStatus(),
        newRole.getId().toString()
    );

    final var updatedUser = updateUserUseCase.execute(user.getId().toString(), dto);

    assertEquals(user.getId(), updatedUser.getId());
    assertEquals(user.getName(), updatedUser.getName());
    assertEquals(user.getStatus(), updatedUser.getStatus().toString());
    assertEquals(dto.roleId(), updatedUser.getRoleId().toString());
    assertNotNull(updatedUser.getCreatedAt());
    assertNotNull(updatedUser.getUpdatedAt());
  }

  @ParameterizedTest
  @CsvSource({
      "null, ACTIVE, name is required",
      "'', ACTIVE, name is required",
      "any_name, null, status is required",
      "null, null, 'name is required, status is required'",
  })
  void shouldNotUpdateUserWithInvalidInput(
      final String name,
      final String status,
      final String expectedMessage
  ) {
    final var actualName = "null".equals(name) ? null : name;
    final var actualStatus = "null".equals(String.valueOf(status)) ? null : status;
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var roleId = role.getId();
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(roleId)));

    final var dto = UpdateUserDto.of(actualName, actualStatus, roleId.toString());

    final var exception = assertThrows(
        ValidationException.class,
        () -> updateUserUseCase.execute(user.getId().toString(), dto)
    );

    assertEquals(expectedMessage, exception.getMessage());
  }
}
