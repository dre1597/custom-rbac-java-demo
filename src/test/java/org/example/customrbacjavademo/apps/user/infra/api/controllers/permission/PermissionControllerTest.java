package org.example.customrbacjavademo.apps.user.infra.api.controllers.permission;

import org.example.customrbacjavademo.apps.user.domain.dto.NewPermissionDto;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.CreatePermissionRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.PermissionResponse;
import org.example.customrbacjavademo.apps.user.usecase.permission.CreatePermissionUseCase;
import org.example.customrbacjavademo.apps.user.usecase.permission.DeletePermissionUseCase;
import org.example.customrbacjavademo.apps.user.usecase.permission.GetOnePermissionUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionControllerTest {
  @Mock
  private CreatePermissionUseCase createPermissionUseCase;

  @Mock
  private GetOnePermissionUseCase getOnePermissionUseCase;

  @Mock
  private DeletePermissionUseCase deletePermissionUseCase;

  @InjectMocks
  private PermissionController controller;


  @Test
  void shouldCreatePermission() {
    final var input = new CreatePermissionRequest(
        "READ",
        "USER",
        "ACTIVE",
        "any_description"
    );

    var dto = NewPermissionDto.from(input);

    var response = controller.create(input);

    verify(createPermissionUseCase).execute(dto);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Test
  void shouldReturnPermissionById() {
    var id = UUID.randomUUID();
    var expected = new PermissionResponse(
        id,
        "READ",
        "USER",
        "any_description",
        "ACTIVE",
        Instant.now(),
        Instant.now()
    );

    when(getOnePermissionUseCase.execute(id.toString())).thenReturn(expected);

    var response = controller.getById(id.toString());

    verify(getOnePermissionUseCase).execute(id.toString());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expected, response.getBody());
  }

  @Test
  void shouldDeletePermission() {
    var id = "perm-id";

    var response = controller.delete(id);

    verify(deletePermissionUseCase).execute(id);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }
}
