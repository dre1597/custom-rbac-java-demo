package org.example.customrbacjavademo.apps.user.usecase.permission;

import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.PermissionResponse;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.example.customrbacjavademo.common.domain.helpers.UUIDValidator;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetOnePermissionUseCase {
  private final PermissionJpaRepository repository;

  public GetOnePermissionUseCase(final PermissionJpaRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  public PermissionResponse execute(final String id) {
    final var idAsUUID = UUIDValidator.parseOrThrow(id);
    
    return repository.findById(idAsUUID)
        .map(PermissionMapper::jpaToResponse)
        .orElseThrow(() -> new NotFoundException("Permission not found"));
  }
}
