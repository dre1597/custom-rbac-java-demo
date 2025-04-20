package org.example.customrbacjavademo.apps.user.infra.api.controllers.user;

import org.example.customrbacjavademo.apps.user.domain.dto.NewUserDto;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdateUserDto;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.CreateUserRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.UpdateUserRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.UserDetailsResponse;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.UserResponse;
import org.example.customrbacjavademo.apps.user.usecase.user.*;
import org.example.customrbacjavademo.common.domain.helpers.Pagination;
import org.example.customrbacjavademo.common.domain.helpers.SearchQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class UserController implements UserAPI {
  private final ListUsersUseCase listUsersUseCase;
  private final CreateUserUseCase createUserUseCase;
  private final GetOneUserUseCase getOneUserUseCase;
  private final UpdateUserUseCase updateUserUseCase;
  private final DeleteUserUseCase deleteUserUseCase;

  public UserController(
      final ListUsersUseCase listUsersUseCase,
      final CreateUserUseCase createUserUseCase,
      final GetOneUserUseCase getOneUserUseCase,
      final UpdateUserUseCase updateUserUseCase,
      final DeleteUserUseCase deleteUserUseCase
  ) {
    this.listUsersUseCase = Objects.requireNonNull(listUsersUseCase);
    this.createUserUseCase = Objects.requireNonNull(createUserUseCase);
    this.getOneUserUseCase = Objects.requireNonNull(getOneUserUseCase);
    this.updateUserUseCase = Objects.requireNonNull(updateUserUseCase);
    this.deleteUserUseCase = Objects.requireNonNull(deleteUserUseCase);
  }

  @Override
  public ResponseEntity<Pagination<UserResponse>> list(
      final String search,
      final int page,
      final int perPage,
      final String sort,
      final String direction
  ) {
    final var searchQuery = new SearchQuery(
        page,
        perPage,
        search,
        sort,
        direction
    );
    return ResponseEntity.ok(listUsersUseCase.execute(searchQuery));
  }

  @Override
  public ResponseEntity<Void> create(final CreateUserRequest input) {
    final var dto = NewUserDto.from(input);
    createUserUseCase.execute(dto);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<UserDetailsResponse> getById(final String id) {
    return ResponseEntity.ok(getOneUserUseCase.execute(id));
  }

  @Override
  public ResponseEntity<Void> update(final String id, final UpdateUserRequest input) {
    final var dto = UpdateUserDto.from(input);
    updateUserUseCase.execute(id, dto);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> delete(final String id) {
    deleteUserUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }
}
