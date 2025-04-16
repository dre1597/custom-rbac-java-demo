package org.example.customrbacjavademo.apps.user.usecase.permission;

import org.example.customrbacjavademo.IntegrationTest;
import org.example.customrbacjavademo.apps.user.domain.dto.NewPermissionDto;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@IntegrationTest
class CreatePermissionUseCaseIntegrationTest {
  @Autowired
  private CreatePermissionUseCase useCase;

  @Transactional
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
    assertEquals(dto.name(), permission.getName());
    assertEquals(dto.scope(), permission.getScope());
    assertEquals(dto.description(), permission.getDescription());
    assertEquals(dto.status(), permission.getStatus());
  }
}
