package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.apps.user.domain.dto.NewUserDto;
import org.example.customrbacjavademo.apps.user.domain.entities.User;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.example.customrbacjavademo.common.domain.helpers.UUIDValidator;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CreateUserUseCase {
  private final UserJpaRepository repository;
  private final RoleJpaRepository roleJpaRepository;

  public CreateUserUseCase(final UserJpaRepository repository, final RoleJpaRepository roleJpaRepository) {
    this.repository = Objects.requireNonNull(repository);
    this.roleJpaRepository = Objects.requireNonNull(roleJpaRepository);
  }

  public User execute(final NewUserDto dto) {
    this.ensureUserIsUnique(dto);
    this.ensureRoleExist(dto);

    final var entity = User.newUser(dto);
    repository.save(UserMapper.entityToJpa(entity));
    return entity;
  }

  private void ensureUserIsUnique(final NewUserDto dto) {
    final var exists = repository.existsByName(dto.name());

    if (exists) {
      throw new AlreadyExistsException("User already exists");
    }
  }

  private void ensureRoleExist(final NewUserDto dto) {
    final var roleIdAsUUID = UUIDValidator.parseOrThrow(dto.roleId());
    final var foundRole = roleJpaRepository.existsById(roleIdAsUUID);

    if (!foundRole) {
      throw new NotFoundException("Role not found");
    }
  }
}
