package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.example.customrbacjavademo.common.domain.helpers.UUIDValidator;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdatePasswordUseCase {
  private final UserJpaRepository repository;

  public UpdatePasswordUseCase(final UserJpaRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  public void execute(final String id, final String password) {
    final var idAsUUID = UUIDValidator.parseOrThrow(id);
    final var userOnDatabase = repository.findById(idAsUUID).orElseThrow(() -> new NotFoundException("User not found"));
    final var user = UserMapper.jpaToEntity(userOnDatabase);

    user.updatePassword(password);
    repository.save(UserMapper.entityToJpa(user));
  }
}
