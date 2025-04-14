package org.example.customrbacjavademo.apps.user.usecase.permission;

import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.common.domain.helpers.UUIDValidator;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DeletePermissionUseCase {
  private final PermissionJpaRepository repository;

  public DeletePermissionUseCase(final PermissionJpaRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  public void execute(final String id) {
    final var idAsUUID = UUIDValidator.parseOrThrow(id);
    final var exists = repository.existsById(idAsUUID);

    if (exists) {
      repository.deleteById(idAsUUID);
    }
  }
}
