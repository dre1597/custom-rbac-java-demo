package org.example.customrbacjavademo.apps.user.infra.api.controllers.role;

import org.example.customrbacjavademo.apps.user.domain.dto.NewRoleDto;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdateRoleDto;
import org.example.customrbacjavademo.apps.user.domain.entities.Role;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.CreateRoleRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.UpdateRoleRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.PermissionResponse;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.RoleDetailsResponse;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.RoleResponse;
import org.example.customrbacjavademo.apps.user.usecase.role.*;
import org.example.customrbacjavademo.common.domain.helpers.Pagination;
import org.example.customrbacjavademo.common.domain.helpers.SearchQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {
  @Mock
  private ListRolesUseCase listRolesUseCase;

  @Mock
  private CreateRoleUseCase createRoleUseCase;

  @Mock
  private GetOneRoleUseCase getOneRoleUseCase;

  @Mock
  private UpdateRoleUseCase updateRoleUseCase;

  @Mock
  private DeleteRoleUseCase deleteRoleUseCase;

  @InjectMocks
  private RoleController controller;

  @Test
  void shouldListRoles() {
    final var roleId = UUID.randomUUID().toString();
    final var expected = new RoleResponse(
        roleId,
        "any_name",
        "any_description",
        "ACTIVE",
        Instant.now(),
        Instant.now()
    );

    final var page = 0;
    final var perPage = 10;
    final var search = "any_name";
    final var sort = "name";
    final var direction = "asc";

    final var pagination = new Pagination<>(
        page,
        perPage,
        1,
        List.of(expected)
    );

    final var query = new SearchQuery(page, perPage, search, sort, direction);

    when(listRolesUseCase.execute(query)).thenReturn(pagination);

    var response = controller.list(search, page, perPage, sort, direction);

    verify(listRolesUseCase).execute(query);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(pagination, response.getBody());
  }

  @Test
  void shouldCreateRole() {
    final var permissions = List.of(UUID.randomUUID().toString());
    final var input = new CreateRoleRequest(
        "any_name",
        "any_description",
        "ACTIVE",
        permissions
    );

    final var dto = NewRoleDto.from(input);
    final var role = Role.newRole(dto);
    final var roleId = role.getId();
    when(createRoleUseCase.execute(dto)).thenReturn(role);

    final var response = controller.create(input);


    verify(createRoleUseCase).execute(dto);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(URI.create("/roles/" + roleId), response.getHeaders().getLocation());
  }

  @Test
  void shouldReturnRoleById() {
    final var id = UUID.randomUUID().toString();
    final var permissionResponse = new PermissionResponse(
        UUID.randomUUID().toString(),
        "READ",
        "USER",
        "any_description",
        "ACTIVE",
        Instant.now(),
        Instant.now()
    );

    final var expected = new RoleDetailsResponse(
        id,
        "any_name",
        "any_description",
        "ACTIVE",
        Instant.now(),
        Instant.now(),
        List.of(permissionResponse)
    );

    when(getOneRoleUseCase.execute(id)).thenReturn(expected);

    final var response = controller.getById(id);

    verify(getOneRoleUseCase).execute(id);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expected, response.getBody());
  }

  @Test
  void shouldUpdateRole() {
    final var id = UUID.randomUUID().toString();
    final var permissions = List.of(UUID.randomUUID().toString());
    final var input = new UpdateRoleRequest(
        "any_name",
        "any_description",
        "INACTIVE",
        permissions
    );

    final var dto = UpdateRoleDto.from(input);
    final var response = controller.update(id, input);

    verify(updateRoleUseCase).execute(id, dto);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void shouldDeleteRole() {
    final var id = UUID.randomUUID().toString();
    final var response = controller.delete(id);

    verify(deleteRoleUseCase).execute(id);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }
}
