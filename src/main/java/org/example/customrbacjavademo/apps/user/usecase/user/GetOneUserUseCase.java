package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.UserDetailsResponse;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class GetOneUserUseCase {
  private final UserJpaRepository repository;

  public GetOneUserUseCase(final UserJpaRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  public UserDetailsResponse execute(final UUID id) {
    final var user = repository.findWithRoleById((id));

    if (user.isEmpty()) {
      throw new NotFoundException("User not found");
    }

    return UserMapper.jpaToDetailsResponse(user.get());
  }
}
