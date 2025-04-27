package org.example.customrbacjavademo.apps.user.infra.api.controllers.role;

import org.example.customrbacjavademo.apps.user.domain.dto.NewRoleDto;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdateRoleDto;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.CreateRoleRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.UpdateRoleRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.RoleDetailsResponse;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.RoleResponse;
import org.example.customrbacjavademo.apps.user.usecase.role.*;
import org.example.customrbacjavademo.common.domain.helpers.Pagination;
import org.example.customrbacjavademo.common.domain.helpers.SearchQuery;
import org.example.customrbacjavademo.configuration.RequiredPermission;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class RoleController implements RoleAPI {
  private final ListRolesUseCase listRolesUseCase;
  private final CreateRoleUseCase createRoleUseCase;
  private final GetOneRoleUseCase getOneRoleUseCase;
  private final UpdateRoleUseCase updateRoleUseCase;
  private final DeleteRoleUseCase deleteRoleUseCase;

  public RoleController(
      final ListRolesUseCase listRolesUseCase,
      final CreateRoleUseCase createRoleUseCase,
      final GetOneRoleUseCase getOneRoleUseCase,
      final UpdateRoleUseCase updateRoleUseCase,
      final DeleteRoleUseCase deleteRoleUseCase
  ) {
    this.listRolesUseCase = Objects.requireNonNull(listRolesUseCase);
    this.createRoleUseCase = Objects.requireNonNull(createRoleUseCase);
    this.getOneRoleUseCase = Objects.requireNonNull(getOneRoleUseCase);
    this.updateRoleUseCase = Objects.requireNonNull(updateRoleUseCase);
    this.deleteRoleUseCase = Objects.requireNonNull(deleteRoleUseCase);
  }

  @Override
  @RequiredPermission(name = PermissionName.READ, scope = PermissionScope.ROLE)
  public ResponseEntity<Pagination<RoleResponse>> list(
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

    return ResponseEntity.ok(listRolesUseCase.execute(searchQuery));
  }

  @Override
  @RequiredPermission(name = PermissionName.CREATE, scope = PermissionScope.ROLE)
  public ResponseEntity<Void> create(final CreateRoleRequest input) {
    final var dto = NewRoleDto.from(input);
    final var role = createRoleUseCase.execute(dto);
    final var location = URI.create("/roles/" + role.getId());
    return ResponseEntity.created(location).build();
  }

  @Override
  @RequiredPermission(name = PermissionName.READ, scope = PermissionScope.ROLE)
  public ResponseEntity<RoleDetailsResponse> getById(final String id) {
    return ResponseEntity.ok(getOneRoleUseCase.execute(id));
  }

  @Override
  @RequiredPermission(name = PermissionName.UPDATE, scope = PermissionScope.ROLE)
  public ResponseEntity<Void> update(final String id, final UpdateRoleRequest input) {
    final var dto = UpdateRoleDto.from(input);
    updateRoleUseCase.execute(id, dto);
    return ResponseEntity.ok().build();
  }

  @Override
  @RequiredPermission(name = PermissionName.DELETE, scope = PermissionScope.ROLE)
  public ResponseEntity<Void> delete(final String id) {
    deleteRoleUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }
}
