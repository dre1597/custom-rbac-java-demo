package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.apps.user.domain.dto.NewUserDto;
import org.example.customrbacjavademo.apps.user.domain.enums.UserStatus;
import org.example.customrbacjavademo.apps.user.domain.services.PasswordService;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {
  @Mock
  private UserJpaRepository repository;

  @Mock
  private RoleJpaRepository roleRepository;

  @InjectMocks
  private CreateUserUseCase useCase;

  @Test
  void shouldCreateUser() {
    final var roleId = UUID.randomUUID();
    final var dto = new NewUserDto("any_name", "any_password", UserStatus.ACTIVE, roleId);

    when(repository.existsByName(dto.name())).thenReturn(false);
    when(roleRepository.existsById(dto.roleId())).thenReturn(false);

    useCase.execute(dto);

    final var userCaptor = ArgumentCaptor.forClass(UserJpaEntity.class);
    verify(repository, times(1)).save(userCaptor.capture());
    final var savedUser = userCaptor.getValue();

    assertEquals(dto.name(), savedUser.getName());
    assertTrue(PasswordService.matches(dto.password(), savedUser.getPassword()));
    assertEquals(dto.status().name(), savedUser.getStatus());
    assertEquals(dto.roleId(), savedUser.getRole().getId());
  }

  @Test
  void shouldNotCreateUserIfNameAlreadyExists() {
    final var dto = new NewUserDto("any_name", "any_password", UserStatus.ACTIVE, UUID.randomUUID());

    when(repository.existsByName(dto.name())).thenReturn(true);

    var exception = assertThrows(AlreadyExistsException.class, () -> useCase.execute(dto));

    assertEquals("User already exists", exception.getMessage());
    verify(repository, never()).save(any());
  }

  @Test
  void shouldThrowIfRoleDoesNotExist() {
    final var invalidId = UUID.randomUUID();
    final var dto = new NewUserDto("any_name", "any_password", UserStatus.ACTIVE, invalidId);

    when(repository.existsByName(dto.name())).thenReturn(false);
    when(roleRepository.existsById(dto.roleId())).thenReturn(true);

    final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(dto));

    assertEquals("Role not found", exception.getMessage());
    verify(repository, never()).save(any());
  }
}
