package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.apps.user.domain.dto.UpdateUserDto;
import org.example.customrbacjavademo.apps.user.domain.entities.User;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.example.customrbacjavademo.common.domain.helpers.UUIDValidator;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateUserUseCase {
  private final UserJpaRepository repository;
  private final RoleJpaRepository roleRepository;

  public UpdateUserUseCase(final UserJpaRepository repository, final RoleJpaRepository roleRepository) {
    this.repository = Objects.requireNonNull(repository);
    this.roleRepository = Objects.requireNonNull(roleRepository);
  }

  public User execute(final String id, final UpdateUserDto dto) {
    final var idAsUUID = UUIDValidator.parseOrThrow(id);
    final var userOnDatabase = repository.findById(idAsUUID).orElseThrow(() -> new NotFoundException("User not found"));
    final var user = UserMapper.jpaToEntity(userOnDatabase);

    this.ensureRoleExist(dto);
    this.ensureUserIsUnique(dto, user);

    user.update(dto);
    repository.save(UserMapper.entityToJpa(user));
    return user;
  }

  private void ensureRoleExist(final UpdateUserDto dto) {
    if (dto.roleId() == null || dto.roleId().isBlank()) {
      throw new ValidationException("roleId is required");
    }

    final var roleIdAsUUID = UUIDValidator.parseOrThrow(dto.roleId());
    final var foundRole = roleRepository.existsById(roleIdAsUUID);

    if (!foundRole) {
      throw new NotFoundException("Role not found");
    }
  }

  private void ensureUserIsUnique(final UpdateUserDto dto, final User user) {
    final var isChangingName = dto.name() != null && !dto.name().equals(user.getName());

    if (isChangingName) {
      final var exists = repository.existsByName(dto.name());

      if (exists) {
        throw new AlreadyExistsException("User already exists");
      }
    }
  }
}
