package org.example.customrbacjavademo.apps.user.usecase.permission;

import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.PermissionResponse;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class GetOnePermissionUseCase {
  private final PermissionJpaRepository repository;

  public GetOnePermissionUseCase(final PermissionJpaRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  public PermissionResponse execute(final UUID id) {
    return repository.findById(id)
        .map(PermissionMapper::jpaToResponse)
        .orElseThrow(() -> new NotFoundException("Permission not found"));
  }
}
