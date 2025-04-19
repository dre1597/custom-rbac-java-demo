package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.UserDetailsResponse;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.example.customrbacjavademo.common.domain.helpers.UUIDValidator;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetOneUserUseCase {
  private final UserJpaRepository repository;

  public GetOneUserUseCase(final UserJpaRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  public UserDetailsResponse execute(final String id) {
    final var idAsUUID = UUIDValidator.parseOrThrow(id);
    final var user = repository.findWithRoleById((idAsUUID));

    if (user.isEmpty()) {
      throw new NotFoundException("User not found");
    }

    return UserMapper.jpaToDetailsResponse(user.get());
  }
}
