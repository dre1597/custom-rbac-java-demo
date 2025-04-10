package org.example.customrbacjavademo.apps.user.usecase.permission;

import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class DeletePermissionUseCase {
  private final PermissionJpaRepository repository;

  public DeletePermissionUseCase(final PermissionJpaRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  public void execute(final UUID id) {
    final var exists = repository.existsById(id);

    if (exists) {
      repository.deleteById(id);
    }
  }
}
