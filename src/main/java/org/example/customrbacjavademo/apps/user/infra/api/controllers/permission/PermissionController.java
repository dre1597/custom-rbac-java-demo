package org.example.customrbacjavademo.apps.user.infra.api.controllers.permission;

import jakarta.validation.Valid;
import org.example.customrbacjavademo.apps.user.domain.dto.NewPermissionDto;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdatePermissionDto;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.CreatePermissionRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.UpdatePermissionRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.PermissionResponse;
import org.example.customrbacjavademo.apps.user.usecase.permission.*;
import org.example.customrbacjavademo.common.domain.helpers.Pagination;
import org.example.customrbacjavademo.common.domain.helpers.SearchQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class PermissionController implements PermissionAPI {
  private final ListPermissionsUseCase listPermissionsUseCase;
  private final CreatePermissionUseCase createPermissionUseCase;
  private final GetOnePermissionUseCase getOnePermissionUseCase;
  private final UpdatePermissionUseCase updatePermissionUseCase;
  private final DeletePermissionUseCase deletePermissionUseCase;

  public PermissionController(
      final ListPermissionsUseCase listPermissionsUseCase,
      final CreatePermissionUseCase createPermissionUseCase,
      final GetOnePermissionUseCase getOnePermissionUseCase,
      final UpdatePermissionUseCase updatePermissionUseCase,
      final DeletePermissionUseCase deletePermissionUseCase
  ) {
    this.listPermissionsUseCase = Objects.requireNonNull(listPermissionsUseCase);
    this.createPermissionUseCase = Objects.requireNonNull(createPermissionUseCase);
    this.getOnePermissionUseCase = Objects.requireNonNull(getOnePermissionUseCase);
    this.updatePermissionUseCase = Objects.requireNonNull(updatePermissionUseCase);
    this.deletePermissionUseCase = Objects.requireNonNull(deletePermissionUseCase);
  }

  @Override
  public ResponseEntity<Pagination<PermissionResponse>> list(
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
    return ResponseEntity.ok(listPermissionsUseCase.execute(searchQuery));
  }

  @Override
  public ResponseEntity<Void> create(final CreatePermissionRequest input) {
    final var dto = NewPermissionDto.from(input);
    final var permission = createPermissionUseCase.execute(dto);
    final var location = URI.create("/permissions/" + permission.getId());
    return ResponseEntity.created(location).build();
  }

  @Override
  public ResponseEntity<PermissionResponse> getById(final String id) {
    return ResponseEntity.ok(getOnePermissionUseCase.execute(id));
  }

  @Override
  public ResponseEntity<Void> update(final String id, @Valid final UpdatePermissionRequest input) {
    final var dto = UpdatePermissionDto.from(input);
    updatePermissionUseCase.execute(id, dto);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> delete(final String id) {
    deletePermissionUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }
}
