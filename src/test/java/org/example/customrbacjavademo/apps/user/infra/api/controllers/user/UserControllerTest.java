package org.example.customrbacjavademo.apps.user.infra.api.controllers.user;

import org.example.customrbacjavademo.apps.user.domain.dto.NewUserDto;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdateUserDto;
import org.example.customrbacjavademo.apps.user.domain.entities.User;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.CreateUserRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.UpdateUserRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.RoleResponse;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.UserDetailsResponse;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.UserResponse;
import org.example.customrbacjavademo.apps.user.usecase.user.*;
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
class UserControllerTest {
  @Mock
  private ListUsersUseCase listUsersUseCase;

  @Mock
  private CreateUserUseCase createUserUseCase;

  @Mock
  private GetOneUserUseCase getOneUserUseCase;

  @Mock
  private UpdateUserUseCase updateUserUseCase;

  @Mock
  private DeleteUserUseCase deleteUserUseCase;

  @InjectMocks
  private UserController controller;


  @Test
  void shouldListUsers() {
    final var userId = UUID.randomUUID().toString();
    final var roleId = UUID.randomUUID().toString();
    final var expected = new UserResponse(
        userId,
        "any_name",
        "ACTIVE",
        Instant.now(),
        Instant.now(),
        roleId
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

    when(listUsersUseCase.execute(query)).thenReturn(pagination);

    var response = controller.list(search, page, perPage, sort, direction);

    verify(listUsersUseCase).execute(query);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(pagination, response.getBody());
  }

  @Test
  void shouldCreateUser() {
    final var roleId = UUID.randomUUID().toString();
    final var input = new CreateUserRequest(
        "any_name",
        "any_password",
        "ACTIVE",
        roleId
    );

    final var dto = NewUserDto.from(input);
    final var user = User.newUser(dto);
    final var userId = user.getId();
    when(createUserUseCase.execute(dto)).thenReturn(user);

    final var response = controller.create(input);

    verify(createUserUseCase).execute(dto);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(URI.create("/users/" + userId), response.getHeaders().getLocation());
  }


  @Test
  void shouldReturnUserById() {
    final var id = UUID.randomUUID().toString();

    final var role = new RoleResponse(
        UUID.randomUUID().toString(),
        "any_name",
        "any_description",
        "ACTIVE",
        Instant.now(),
        Instant.now()
    );

    final var expected = new UserDetailsResponse(
        id,
        "any_name",
        "ACTIVE",
        Instant.now(),
        Instant.now(),
        role
    );

    when(getOneUserUseCase.execute(id)).thenReturn(expected);

    final var response = controller.getById(id);

    verify(getOneUserUseCase).execute(id);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expected, response.getBody());
  }

  @Test
  void shouldUpdateUser() {
    final var id = UUID.randomUUID().toString();
    final var roleId = UUID.randomUUID().toString();
    final var input = new UpdateUserRequest(
        "any_name",
        "INACTIVE",
        roleId
    );

    final var dto = UpdateUserDto.from(input);
    final var response = controller.update(id, input);

    verify(updateUserUseCase).execute(id, dto);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void shouldDeleteUser() {
    final var id = UUID.randomUUID().toString();
    final var response = controller.delete(id);

    verify(deleteUserUseCase).execute(id);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }
}
