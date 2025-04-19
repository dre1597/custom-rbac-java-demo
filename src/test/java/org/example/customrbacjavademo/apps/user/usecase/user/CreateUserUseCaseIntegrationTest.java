package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.IntegrationTest;
import org.example.customrbacjavademo.apps.user.domain.dto.NewUserDto;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.RoleTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.UserTestMocks;
import org.example.customrbacjavademo.apps.user.domain.services.PasswordService;
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
class CreateUserUseCaseIntegrationTest {
  @Autowired
  private CreateUserUseCase useCase;

  @Autowired
  private UserJpaRepository repository;

  @Autowired
  private RoleJpaRepository roleRepository;

  @Autowired
  private PermissionJpaRepository permissionRepository;

  @Test
  void shouldCreateUser() {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));

    final var dto = NewUserDto.of(
        "any_name",
        "any_password",
        "ACTIVE",
        role.getId().toString()
    );

    final var user = useCase.execute(dto);

    assertNotNull(user.getId());
    assertEquals(dto.name(), user.getName());
    assertTrue(PasswordService.matches(dto.password(), user.getPassword()));
    assertEquals(dto.status(), user.getStatus().toString());
    assertEquals(dto.roleId(), user.getRoleId().toString());
    assertNotNull(user.getCreatedAt());
    assertNotNull(user.getUpdatedAt());
  }

  @Test
  void shouldNotCreateUserIfNameAlreadyExists() {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var dto = NewUserDto.of(
        user.getName(),
        "any_password",
        "ACTIVE",
        role.getId().toString()
    );

    final var exception = assertThrows(
        AlreadyExistsException.class,
        () -> useCase.execute(dto)
    );

    assertEquals("User already exists", exception.getMessage());
  }

  @Test
  void shouldThrowIfRoleDoesNotExist() {
    final var dto = NewUserDto.of(
        "any_name",
        "any_password",
        "ACTIVE",
        UUID.randomUUID().toString()
    );

    final var exception = assertThrows(
        NotFoundException.class,
        () -> useCase.execute(dto)
    );

    assertEquals("Role not found", exception.getMessage());
  }

  @ParameterizedTest
  @CsvSource({
      "null, any_password, ACTIVE, name is required",
      "'', any_password, ACTIVE, name is required",
      "any_name, null, ACTIVE, password is required",
      "any_name, '', ACTIVE, password is required",
      "any_name, any_password, null, status is required",
      "null, null, null, 'name is required, password is required, status is required'",
  })
  void shouldNotCreateUserWithInvalidInput(
      final String name,
      final String password,
      final String status,
      final String expectedMessage
  ) {
    final var actualName = "null".equals(name) ? null : name;
    final var actualPassword = "null".equals(password) ? null : password;
    final var actualStatus = "null".equals(String.valueOf(status)) ? null : status;
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var roleId = role.getId().toString();

    final var dto = NewUserDto.of(actualName, actualPassword, actualStatus, roleId);

    final var exception = assertThrows(
        ValidationException.class,
        () -> useCase.execute(dto)
    );

    assertEquals(expectedMessage, exception.getMessage());
  }
}
