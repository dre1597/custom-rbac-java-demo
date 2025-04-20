package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.apps.user.domain.entities.User;
import org.example.customrbacjavademo.apps.user.domain.services.PasswordService;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;
import org.example.customrbacjavademo.common.domain.exceptions.UnauthorizedException;
import org.example.customrbacjavademo.common.domain.helpers.UUIDValidator;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdatePasswordUseCase {
  private final UserJpaRepository repository;

  public UpdatePasswordUseCase(final UserJpaRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  public void execute(final String id, final String oldPassword, final String newPassword) {
    final var idAsUUID = UUIDValidator.parseOrThrow(id);
    final var userOnDatabase = repository.findById(idAsUUID).orElseThrow(() -> new UnauthorizedException("User not found or old password is invalid"));
    final var user = UserMapper.jpaToEntity(userOnDatabase);

    this.ensureOldPasswordIsValid(user, oldPassword);

    user.updatePassword(newPassword);
    repository.save(UserMapper.entityToJpa(user));
  }

  public void ensureOldPasswordIsValid(final User user, final String oldPassword) {
    if (!PasswordService.matches(oldPassword, user.getPassword())) {
      throw new UnauthorizedException("User not found or old password is invalid");
    }
  }
}
