package org.example.customrbacjavademo.apps.user.infra.api.controllers.permission;

import org.example.customrbacjavademo.apps.user.domain.dto.NewPermissionDto;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.CreatePermissionRequest;
import org.example.customrbacjavademo.apps.user.usecase.permission.CreatePermissionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class PermissionController implements PermissionAPI {
  private final CreatePermissionUseCase createPermissionUseCase;

  public PermissionController(final CreatePermissionUseCase createPermissionUseCase) {
    this.createPermissionUseCase = Objects.requireNonNull(createPermissionUseCase);
  }

  @Override
  public ResponseEntity<Void> create(final CreatePermissionRequest input) {
    final var dto = NewPermissionDto.from(input);

    createPermissionUseCase.execute(dto);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
