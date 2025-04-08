package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class DeleteUserUseCase {
  private final UserJpaRepository repository;

  public DeleteUserUseCase(final UserJpaRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  public void execute(final UUID id) {
    repository.findById(id)
        .ifPresent(user -> repository.deleteById(id));
  }
}
