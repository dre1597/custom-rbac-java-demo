package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.common.domain.helpers.UUIDValidator;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DeleteRoleUseCase {
  private final RoleJpaRepository repository;

  public DeleteRoleUseCase(final RoleJpaRepository repository) {
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
