package org.example.customrbacjavademo.apps.user.infra.api.controllers.permission;

import org.example.customrbacjavademo.apps.user.domain.dto.NewPermissionDto;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdatePermissionDto;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.CreatePermissionRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.UpdatePermissionRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.PermissionResponse;
import org.example.customrbacjavademo.apps.user.usecase.permission.*;
import org.example.customrbacjavademo.common.domain.helpers.Pagination;
import org.example.customrbacjavademo.common.domain.helpers.SearchQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionControllerTest {
  @Mock
  private ListPermissionsUseCase listPermissionsUseCase;

  @Mock
  private CreatePermissionUseCase createPermissionUseCase;

  @Mock
  private GetOnePermissionUseCase getOnePermissionUseCase;

  @Mock
  private UpdatePermissionUseCase updatePermissionUseCase;

  @Mock
  private DeletePermissionUseCase deletePermissionUseCase;

  @InjectMocks
  private PermissionController controller;

  @Test
  void shouldListPermissions() {
    final var permissionId = UUID.randomUUID();
    final var expected = new PermissionResponse(
        permissionId,
        "READ",
        "USER",
        "any_description",
        "ACTIVE",
        Instant.now(),
        Instant.now()
    );

    final var page = 0;
    final var perPage = 10;
    final var search = "USER";
    final var sort = "name";
    final var direction = "asc";

    final var pagination = new Pagination<>(
        page,
        perPage,
        1,
        List.of(expected)
    );

    final var query = new SearchQuery(page, perPage, search, sort, direction);

    when(listPermissionsUseCase.execute(query)).thenReturn(pagination);

    final var response = controller.list(search, page, perPage, sort, direction);

    verify(listPermissionsUseCase).execute(query);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(pagination, response.getBody());
  }


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
  void shouldUpdatePermission() {
    var id = UUID.randomUUID().toString();
    var input = new UpdatePermissionRequest(
        "CREATE",
        "USER",
        "INACTIVE",
        "updated_description"
    );

    var dto = UpdatePermissionDto.from(input);

    var response = controller.update(id, input);

    verify(updatePermissionUseCase).execute(id, dto);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void shouldDeletePermission() {
    var id = "perm-id";

    var response = controller.delete(id);

    verify(deletePermissionUseCase).execute(id);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }
}
