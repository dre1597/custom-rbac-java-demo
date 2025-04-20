package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.apps.user.domain.mocks.UserTestMocks;
import org.example.customrbacjavademo.apps.user.domain.services.PasswordService;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;
import org.example.customrbacjavademo.common.domain.exceptions.UnauthorizedException;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
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
class UpdatePasswordUseCaseTest {
  @Mock
  private UserJpaRepository repository;

  @InjectMocks
  private UpdatePasswordUseCase useCase;

  @Test
  void shouldUpdatePassword() {
    final var oldPassword = "any_password";
    final var newPassword = "updated_password";
    final var user = UserTestMocks.createActiveTestUser("any_name", oldPassword);

    when(repository.findById(user.getId())).thenReturn(Optional.of(UserMapper.entityToJpa(user)));

    useCase.execute(user.getId().toString(), oldPassword, newPassword);

    final var userJpaEntityCaptor = ArgumentCaptor.forClass(UserJpaEntity.class);
    verify(repository, times(1)).save(userJpaEntityCaptor.capture());
    final var savedUser = userJpaEntityCaptor.getValue();

    assertNotNull(savedUser.getPassword());
    assertNotEquals(user.getPassword(), savedUser.getPassword());
    assertTrue(PasswordService.matches(newPassword, savedUser.getPassword()));
  }

  @Test
  void shouldThrowWhenUserNotFound() {
    final var id = UUID.randomUUID();

    when(repository.findById(id)).thenReturn(Optional.empty());

    final var exception = assertThrows(UnauthorizedException.class, () -> useCase.execute(id.toString(), "any_password", "any_password"));
    assertEquals("User not found or old password is invalid", exception.getMessage());
  }

  @Test
  void shouldThrowWhenPasswordIsBlank() {
    final var id = UUID.randomUUID();
    final var user = UserTestMocks.createActiveTestUser(UUID.randomUUID());

    when(repository.findById(id)).thenReturn(Optional.of(UserMapper.entityToJpa(user)));

    final var exception = assertThrows(ValidationException.class, () -> useCase.execute(id.toString(), "any_password", " "));
    assertEquals("password is required", exception.getMessage());
  }

  @Test
  void shouldThrowWhenPasswordIsNull() {
    final var id = UUID.randomUUID();
    final var user = UserTestMocks.createActiveTestUser(UUID.randomUUID());

    when(repository.findById(id)).thenReturn(Optional.of(UserMapper.entityToJpa(user)));

    final var exception = assertThrows(ValidationException.class, () -> useCase.execute(id.toString(), "any_password", null));
    assertEquals("password is required", exception.getMessage());
  }

  @Test
  void shouldThrowWhenOldPasswordIsInvalid() {
    final var oldPassword = "any_password";
    final var user = UserTestMocks.createActiveTestUser(oldPassword, "new_password");

    when(repository.findById(user.getId())).thenReturn(Optional.of(UserMapper.entityToJpa(user)));

    final var exception = assertThrows(
        UnauthorizedException.class,
        () -> useCase.execute(user.getId().toString(), "wrong_password", "new_password")
    );

    assertEquals("User not found or old password is invalid", exception.getMessage());
  }
}
