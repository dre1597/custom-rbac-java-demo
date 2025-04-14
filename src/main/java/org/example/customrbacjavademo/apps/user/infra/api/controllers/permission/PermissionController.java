package org.example.customrbacjavademo.apps.user.infra.api.controllers.permission;

import org.example.customrbacjavademo.apps.user.domain.dto.NewPermissionDto;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.CreatePermissionRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.PermissionResponse;
import org.example.customrbacjavademo.apps.user.usecase.permission.CreatePermissionUseCase;
import org.example.customrbacjavademo.apps.user.usecase.permission.GetOnePermissionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class PermissionController implements PermissionAPI {
  private final CreatePermissionUseCase createPermissionUseCase;
  private final GetOnePermissionUseCase getOnePermissionUseCase;

  public PermissionController(
      final CreatePermissionUseCase createPermissionUseCase,
      final GetOnePermissionUseCase getOnePermissionUseCase
  ) {
    this.createPermissionUseCase = Objects.requireNonNull(createPermissionUseCase);
    this.getOnePermissionUseCase = Objects.requireNonNull(getOnePermissionUseCase);
  }

  @Override
  public ResponseEntity<Void> create(final CreatePermissionRequest input) {
    final var dto = NewPermissionDto.from(input);

    createPermissionUseCase.execute(dto);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Override
  public ResponseEntity<PermissionResponse> getById(final String id) {
    return ResponseEntity.ok(getOnePermissionUseCase.execute(id));
  }
}
