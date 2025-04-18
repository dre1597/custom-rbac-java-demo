package org.example.customrbacjavademo.apps.user.usecase.permission;

import org.example.customrbacjavademo.IntegrationTest;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdatePermissionDto;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionStatus;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.example.customrbacjavademo.common.domain.helpers.EnumValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@IntegrationTest
class UpdatePermissionUseCaseIntegrationTest {
  @Autowired
  private UpdatePermissionUseCase useCase;

  @Autowired
  private PermissionJpaRepository repository;

  @Test
  void shouldUpdatePermission() {
    final var savedPermission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    final var dto = UpdatePermissionDto.of(
        PermissionName.UPDATE.toString(),
        PermissionScope.PERMISSION.toString(),
        "updated_description",
        PermissionStatus.INACTIVE.toString()
    );

    final var updatedPermission = useCase.execute(savedPermission.getId().toString(), dto);

    assertEquals(dto.name(), updatedPermission.getName().name());
    assertEquals(dto.scope(), updatedPermission.getScope().name());
    assertEquals(dto.description(), updatedPermission.getDescription());
    assertEquals(dto.status(), updatedPermission.getStatus().name());
  }

  @Test
  void shouldNotUpdateNonExistentPermission() {
    final var id = UUID.randomUUID().toString();
    final var dto = UpdatePermissionDto.of(
        PermissionName.UPDATE.toString(),
        PermissionScope.PERMISSION.toString(),
        "updated_description",
        PermissionStatus.INACTIVE.toString()
    );

    final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(id, dto));

    assertEquals("Permission not found", exception.getMessage());
  }

  @Test
  void shouldNotUpdateWithInvalidId() {
    final var id = "invalid_uuid";
    final var dto = UpdatePermissionDto.of(
        PermissionName.UPDATE.toString(),
        PermissionScope.PERMISSION.toString(),
        "updated_description",
        PermissionStatus.INACTIVE.toString()
    );

    final var exception = assertThrows(ValidationException.class, () -> useCase.execute(id, dto));

    assertEquals("Invalid UUID: invalid_uuid", exception.getMessage());
  }

  @Test
  void shouldNotUpdateToDuplicateNameAndScope() {
    final var savedPermission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var permissionToUpdate = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission(PermissionName.UPDATE.name())));


    final var dto = UpdatePermissionDto.of(
        savedPermission.getName(),
        savedPermission.getScope(),
        permissionToUpdate.getDescription(),
        permissionToUpdate.getStatus()
    );

    final var exception = assertThrows(AlreadyExistsException.class, () -> useCase.execute(permissionToUpdate.getId().toString(), dto));
    assertEquals("Permission already exists", exception.getMessage());
  }

  @Test
  void shouldUpdateWhenOnlyNameChanged() {
    final var savedPermission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    final var dto = UpdatePermissionDto.of(
        PermissionName.UPDATE.toString(),
        savedPermission.getScope(),
        savedPermission.getDescription(),
        savedPermission.getStatus()
    );

    final var updatedPermission = useCase.execute(savedPermission.getId().toString(), dto);

    assertEquals(dto.name(), updatedPermission.getName().name());
    assertEquals(dto.scope(), updatedPermission.getScope().name());
    assertEquals(dto.description(), updatedPermission.getDescription());
    assertEquals(dto.status(), updatedPermission.getStatus().name());
  }

  @Test
  void shouldUpdateWhenOnlyScopeChanged() {
    final var savedPermission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    final var dto = UpdatePermissionDto.of(
        savedPermission.getName(),
        PermissionScope.PERMISSION.toString(),
        savedPermission.getDescription(),
        savedPermission.getStatus()
    );

    final var updatedPermission = useCase.execute(savedPermission.getId().toString(), dto);

    assertEquals(dto.name(), updatedPermission.getName().name());
    assertEquals(dto.scope(), updatedPermission.getScope().name());
    assertEquals(dto.description(), updatedPermission.getDescription());
    assertEquals(dto.status(), updatedPermission.getStatus().name());
  }

  @ParameterizedTest
  @MethodSource("invalidInputProvider")
  void shouldNotUpdatePermissionWithInvalidInput(
      final String name,
      final String scope,
      final String description,
      final String status,
      final String expectedMessage
  ) {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    final var actualName = "null".equals(name) ? null : name;
    final var actualScope = "null".equals(scope) ? null : scope;
    final var actualDescription = "null".equals(description) ? null : description;
    final var actualStatus = "null".equals(status) ? null : status;

    final var dto = UpdatePermissionDto.of(
        actualName,
        actualScope,
        actualDescription,
        actualStatus
    );

    final var exception = assertThrows(ValidationException.class, () -> useCase.execute(permission.getId().toString(), dto));
    assertEquals(expectedMessage, exception.getMessage());
  }

  private static Stream<Arguments> invalidInputProvider() {
    final var validNames = EnumValidator.enumValuesAsString(PermissionName.class);
    final var validScopes = EnumValidator.enumValuesAsString(PermissionScope.class);
    final var validStatuses = EnumValidator.enumValuesAsString(PermissionStatus.class);

    return Stream.of(
        Arguments.of("null", "USER", "any_description", "ACTIVE", "name is required"),
        Arguments.of("INVALID", "USER", "any_description", "ACTIVE", "name must be one of " + validNames),
        Arguments.of("READ", "null", "any_description", "ACTIVE", "scope is required"),
        Arguments.of("READ", "INVALID", "any_description", "ACTIVE", "scope must be one of " + validScopes),
        Arguments.of("READ", "USER", "null", "ACTIVE", "description is required"),
        Arguments.of("READ", "USER", "", "ACTIVE", "description is required"),
        Arguments.of("READ", "USER", "any_description", "null", "status is required"),
        Arguments.of("READ", "USER", "any_description", "INVALID", "status must be one of " + validStatuses),
        Arguments.of("null", "null", "null", "null", "name is required, scope is required, description is required, status is required")
    );
  }
}
