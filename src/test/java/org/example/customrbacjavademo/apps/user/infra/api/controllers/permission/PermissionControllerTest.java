package org.example.customrbacjavademo.apps.user.infra.api.controllers.permission;

import org.example.customrbacjavademo.apps.user.domain.dto.NewPermissionDto;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.CreatePermissionRequest;
import org.example.customrbacjavademo.apps.user.usecase.permission.CreatePermissionUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PermissionControllerTest {
  @Mock
  private CreatePermissionUseCase createPermissionUseCase;

  @InjectMocks
  private PermissionController controller;

  @Test
  void shouldCreatePermission() {
    final var input = new CreatePermissionRequest("READ", "USER", "ACTIVE", "any_description");

    final var response = controller.create(input);

    final var capturedPermissionDto = ArgumentCaptor.forClass(NewPermissionDto.class);
    verify(createPermissionUseCase).execute(capturedPermissionDto.capture());

    final var dto = capturedPermissionDto.getValue();
    assertEquals(input.name(), dto.name());
    assertEquals(input.scope(), dto.scope());
    assertEquals(input.description(), dto.description());
    assertEquals(input.status(), dto.status());

    assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode());
    assertNull(response.getBody());
  }
}
