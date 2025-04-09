package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.apps.user.domain.dto.UpdateUserDto;
import org.example.customrbacjavademo.apps.user.domain.enums.UserStatus;
import org.example.customrbacjavademo.apps.user.domain.mocks.UserTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseTest {
  @Mock
  private UserJpaRepository repository;

  @Mock
  private RoleJpaRepository roleRepository;

  @InjectMocks
  private UpdateUserUseCase useCase;

  @Test
  void shouldUpdateUser() {
    final var id = UUID.randomUUID();
    final var roleId = UUID.randomUUID();
    final var dto = UpdateUserDto.of("updated_name", UserStatus.INACTIVE, roleId);

    final var user = UserTestMocks.createActiveTestUser(roleId);

    when(repository.findById(id)).thenReturn(Optional.of(UserMapper.entityToJpa(user)));
    when(roleRepository.existsById(roleId)).thenReturn(true);

    useCase.execute(id, dto);

    final var userJpaEntityCaptor = ArgumentCaptor.forClass(UserJpaEntity.class);
    verify(repository, times(1)).save(userJpaEntityCaptor.capture());
    final var savedUser = userJpaEntityCaptor.getValue();

    assertEquals(dto.name(), savedUser.getName());
    assertEquals(dto.status().name(), savedUser.getStatus());
    assertEquals(dto.roleId(), savedUser.getRole().getId());
    assertNotNull(savedUser.getCreatedAt());
    assertNotNull(savedUser.getUpdatedAt());
  }

  @Test
  void shouldNotUpdateNonExistentUser() {
    final var id = UUID.randomUUID();
    final var roleId = UUID.randomUUID();
    final var dto = UpdateUserDto.of("any_name", UserStatus.ACTIVE, roleId);

    when(repository.findById(id)).thenReturn(Optional.empty());

    final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(id, dto));
    assertEquals("User not found", exception.getMessage());
  }

  @Test
  void shouldNotUpdateUserWithNonExistentRole() {
    final var id = UUID.randomUUID();
    final var roleId = UUID.randomUUID();
    final var dto = UpdateUserDto.of("any_name", UserStatus.ACTIVE, roleId);

    when(repository.findById(id)).thenReturn(Optional.of(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(roleId))));
    when(roleRepository.existsById(roleId)).thenReturn(false);

    final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(id, dto));
    assertEquals("Role not found", exception.getMessage());
  }

  @Test
  void shouldNotUpdateToDuplicatedName() {
    final var roleId = UUID.randomUUID();
    final var dto = UpdateUserDto.of("new_name", UserStatus.ACTIVE, roleId);

    final var user = UserTestMocks.createActiveTestUser(roleId);
    final var id = user.getId();

    when(repository.findById(id)).thenReturn(Optional.of(UserMapper.entityToJpa(user)));
    when(roleRepository.existsById(roleId)).thenReturn(true);
    when(repository.existsByName(dto.name())).thenReturn(true);

    final var exception = assertThrows(AlreadyExistsException.class, () -> useCase.execute(id, dto));
    assertEquals("User already exists", exception.getMessage());
  }

  @Test
  void shouldUpdateWhenOnlyNameChanged() {
    final var id = UUID.randomUUID();
    final var roleId = UUID.randomUUID();
    final var dto = UpdateUserDto.of("new_name", UserStatus.ACTIVE, roleId);

    final var user = UserTestMocks.createActiveTestUser(roleId);

    when(repository.findById(id)).thenReturn(Optional.of(UserMapper.entityToJpa(user)));
    when(roleRepository.existsById(roleId)).thenReturn(true);

    useCase.execute(id, dto);

    final var userJpaEntityCaptor = ArgumentCaptor.forClass(UserJpaEntity.class);
    verify(repository, times(1)).save(userJpaEntityCaptor.capture());
    final var savedUser = userJpaEntityCaptor.getValue();

    assertEquals(dto.name(), savedUser.getName());
    assertEquals(dto.status().name(), savedUser.getStatus());
    assertEquals(dto.roleId(), savedUser.getRole().getId());
    assertNotNull(savedUser.getCreatedAt());
    assertNotNull(savedUser.getUpdatedAt());
  }

  @Test
  void shouldUpdateRole() {
    final var id = UUID.randomUUID();
    final var oldRoleId = UUID.randomUUID();
    final var newRoleId = UUID.randomUUID();
    final var dto = UpdateUserDto.of("any_name", UserStatus.ACTIVE, newRoleId);

    final var user = UserTestMocks.createActiveTestUser(oldRoleId);

    when(repository.findById(id)).thenReturn(Optional.of(UserMapper.entityToJpa(user)));
    when(roleRepository.existsById(newRoleId)).thenReturn(true);

    useCase.execute(id, dto);

    final var userJpaEntityCaptor = ArgumentCaptor.forClass(UserJpaEntity.class);
    verify(repository, times(1)).save(userJpaEntityCaptor.capture());
    final var savedUser = userJpaEntityCaptor.getValue();

    assertEquals(dto.name(), savedUser.getName());
    assertEquals(dto.status().name(), savedUser.getStatus());
    assertEquals(dto.roleId(), newRoleId);
    assertNotNull(savedUser.getCreatedAt());
    assertNotNull(savedUser.getUpdatedAt());
  }
}
