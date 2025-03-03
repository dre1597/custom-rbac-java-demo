package org.example.customrbacjavademo.apps.user.usecase;

import org.example.customrbacjavademo.apps.user.domain.entities.Permission;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.mappers.PermissionMapper;
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

  public Permission execute(final UUID id) {
    return repository.findById(id)
        .map(PermissionMapper::jpaToEntity)
        .orElseThrow(() -> new NotFoundException("Permission not found"));
  }
}
