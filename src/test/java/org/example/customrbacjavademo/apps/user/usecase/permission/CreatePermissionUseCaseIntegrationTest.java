package org.example.customrbacjavademo.apps.user.usecase.permission;

import org.example.customrbacjavademo.IntegrationTest;
import org.example.customrbacjavademo.apps.user.domain.dto.NewPermissionDto;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionStatus;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.example.customrbacjavademo.common.domain.helpers.EnumUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
class CreatePermissionUseCaseIntegrationTest {
  @Autowired
  private CreatePermissionUseCase useCase;

  @Autowired
  private PermissionJpaRepository repository;

  @Test
  void shouldCreatePermission() {
    final var dto = NewPermissionDto.of(
        PermissionName.READ.name(),
        PermissionScope.USER.name(),
        "any_description",
        PermissionStatus.ACTIVE.name()
    );

    final var permission = useCase.execute(dto);

    assertNotNull(permission.getId());
    assertEquals(dto.name(), permission.getName().name());
    assertEquals(dto.scope(), permission.getScope().name());
    assertEquals(dto.description(), permission.getDescription());
    assertEquals(dto.status(), permission.getStatus().name());
  }

  @Test
  void shouldNotCreatePermissionWithSameNameAndScopeTogether() {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    final var dto = NewPermissionDto.of(
        permission.getName(),
        permission.getScope(),
        "any_description",
        PermissionStatus.ACTIVE.name()
    );

    final var exception = assertThrows(AlreadyExistsException.class, () -> useCase.execute(dto));
    assertEquals("Permission already exists", exception.getMessage());
  }

  @ParameterizedTest
  @MethodSource("invalidInputProvider")
  void shouldNotCreatePermissionWithInvalidInput(
      final String name,
      final String scope,
      final String description,
      final String status,
      final String expectedMessage
  ) {
    final var actualName = "null".equals(name) ? null : name;
    final var actualScope = "null".equals(scope) ? null : scope;
    final var actualDescription = "null".equals(description) ? null : description;
    final var actualStatus = "null".equals(status) ? null : status;

    final var dto = NewPermissionDto.of(actualName, actualScope, actualDescription, actualStatus);
    final var exception = assertThrows(ValidationException.class, () -> useCase.execute(dto));
    assertEquals(expectedMessage, exception.getMessage());
  }

  private static Stream<Arguments> invalidInputProvider() {
    final var validNames = EnumUtils.enumValuesAsString(PermissionName.class);
    final var validScopes = EnumUtils.enumValuesAsString(PermissionScope.class);
    final var validStatuses = EnumUtils.enumValuesAsString(PermissionStatus.class);

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
