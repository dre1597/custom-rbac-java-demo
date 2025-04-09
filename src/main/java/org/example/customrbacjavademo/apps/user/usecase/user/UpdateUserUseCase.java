package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.apps.user.domain.dto.UpdateUserDto;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class UpdateUserUseCase {
  private final UserJpaRepository repository;
  private final RoleJpaRepository roleRepository;

  public UpdateUserUseCase(final UserJpaRepository repository, final RoleJpaRepository roleRepository) {
    this.repository = Objects.requireNonNull(repository);
    this.roleRepository = Objects.requireNonNull(roleRepository);
  }

  public void execute(final UUID id, final UpdateUserDto dto) {
    final var userOnDatabase = repository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

    final var foundRole = roleRepository.existsById(dto.roleId());

    if (!foundRole) {
      throw new NotFoundException("Role not found");
    }

    final var user = UserMapper.jpaToEntity(userOnDatabase);
    final var isChangingName = !dto.name().equals(user.getName());

    if (isChangingName) {
      final var exists = repository.existsByName(dto.name());

      if (exists) {
        throw new AlreadyExistsException("User already exists");
      }
    }

    user.update(dto);
    repository.save(UserMapper.entityToJpa(user));
  }
}
