package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class DeleteRoleUseCase {
  private final RoleJpaRepository repository;

  public DeleteRoleUseCase(final RoleJpaRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  public void execute(final UUID id) {
    final var exists = repository.existsById(id);

    if (exists) {
      repository.deleteById(id);
    }
  }
}
