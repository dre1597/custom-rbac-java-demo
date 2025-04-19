package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.common.domain.helpers.UUIDValidator;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DeleteUserUseCase {
  private final UserJpaRepository repository;

  public DeleteUserUseCase(final UserJpaRepository repository) {
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
