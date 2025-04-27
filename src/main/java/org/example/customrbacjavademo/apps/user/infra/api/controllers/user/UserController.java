package org.example.customrbacjavademo.apps.user.infra.api.controllers.user;

import org.example.customrbacjavademo.apps.user.domain.dto.NewUserDto;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdateUserDto;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.CreateUserRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.UpdatePasswordRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.UpdateUserRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.UserDetailsResponse;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.UserResponse;
import org.example.customrbacjavademo.apps.user.usecase.user.*;
import org.example.customrbacjavademo.common.domain.helpers.Pagination;
import org.example.customrbacjavademo.common.domain.helpers.SearchQuery;
import org.example.customrbacjavademo.configuration.RequiredPermission;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class UserController implements UserAPI {
  private final ListUsersUseCase listUsersUseCase;
  private final CreateUserUseCase createUserUseCase;
  private final GetOneUserUseCase getOneUserUseCase;
  private final UpdateUserUseCase updateUserUseCase;
  private final UpdatePasswordUseCase updatePasswordUseCase;
  private final DeleteUserUseCase deleteUserUseCase;

  public UserController(
      final ListUsersUseCase listUsersUseCase,
      final CreateUserUseCase createUserUseCase,
      final GetOneUserUseCase getOneUserUseCase,
      final UpdateUserUseCase updateUserUseCase,
      final UpdatePasswordUseCase updatePasswordUseCase,
      final DeleteUserUseCase deleteUserUseCase
  ) {
    this.listUsersUseCase = Objects.requireNonNull(listUsersUseCase);
    this.createUserUseCase = Objects.requireNonNull(createUserUseCase);
    this.getOneUserUseCase = Objects.requireNonNull(getOneUserUseCase);
    this.updateUserUseCase = Objects.requireNonNull(updateUserUseCase);
    this.updatePasswordUseCase = Objects.requireNonNull(updatePasswordUseCase);
    this.deleteUserUseCase = Objects.requireNonNull(deleteUserUseCase);
  }

  @Override
  @RequiredPermission(name = PermissionName.READ, scope = PermissionScope.USER)
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
  @RequiredPermission(name = PermissionName.CREATE, scope = PermissionScope.USER)
  public ResponseEntity<Void> create(final CreateUserRequest input) {
    final var dto = NewUserDto.from(input);
    final var user = createUserUseCase.execute(dto);
    final var location = URI.create("/users/" + user.getId());
    return ResponseEntity.created(location).build();
  }

  @Override
  @RequiredPermission(name = PermissionName.READ, scope = PermissionScope.USER)
  public ResponseEntity<UserDetailsResponse> getById(final String id) {
    return ResponseEntity.ok(getOneUserUseCase.execute(id));
  }

  @Override
  @RequiredPermission(name = PermissionName.UPDATE, scope = PermissionScope.USER)
  public ResponseEntity<Void> update(final String id, final UpdateUserRequest input) {
    final var dto = UpdateUserDto.from(input);
    updateUserUseCase.execute(id, dto);
    return ResponseEntity.ok().build();
  }

  @Override
  @RequiredPermission(name = PermissionName.UPDATE_PASSWORD, scope = PermissionScope.USER)
  public ResponseEntity<Void> updatePassword(final String id, final UpdatePasswordRequest input) {
    updatePasswordUseCase.execute(id, input.oldPassword(), input.newPassword());
    return ResponseEntity.ok().build();
  }

  @Override
  @RequiredPermission(name = PermissionName.DELETE, scope = PermissionScope.USER)
  public ResponseEntity<Void> delete(final String id) {
    deleteUserUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }
}
