package org.example.customrbacjavademo.apps.user.infra.api.controllers.permission;

import org.example.customrbacjavademo.apps.user.domain.dto.NewPermissionDto;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.CreatePermissionRequest;
import org.example.customrbacjavademo.apps.user.usecase.permission.CreatePermissionUseCase;
import org.example.customrbacjavademo.apps.user.usecase.permission.GetOnePermissionUseCase;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionControllerTest {
  @Mock
  private CreatePermissionUseCase createPermissionUseCase;

  @Mock
  private GetOnePermissionUseCase getOnePermissionUseCase;

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

  @Test
  void shouldFindPermissionById() {
    final var id = UUID.randomUUID();
    final var permission = PermissionTestMocks.createActiveTestPermission();

    when(getOnePermissionUseCase.execute(id.toString()))
        .thenReturn(PermissionMapper.entityToResponse(permission));

    final var result = controller.getById(id.toString());
    final var body = result.getBody();

    verify(getOnePermissionUseCase).execute(id.toString());
    assert body != null;
    assertEquals(permission.getId(), body.id());
    assertEquals(permission.getName().name(), body.name());
    assertEquals(permission.getScope().name(), body.scope());
    assertEquals(permission.getDescription(), body.description());
    assertEquals(permission.getStatus().name(), body.status());
    assertEquals(permission.getCreatedAt(), body.createdAt());
    assertEquals(permission.getUpdatedAt(), body.updatedAt());
  }
}
