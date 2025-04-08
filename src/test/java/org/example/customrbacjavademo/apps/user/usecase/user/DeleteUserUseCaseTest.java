package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.apps.user.domain.mocks.UserTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserUseCaseTest {
  @Mock
  private UserJpaRepository repository;

  @InjectMocks
  private DeleteUserUseCase useCase;

  @Test
  void shouldDeleteUser() {
    final var id = UUID.randomUUID();
    final var user = UserTestMocks.createActiveTestUser();

    when(repository.existsById(id)).thenReturn(true);

    useCase.execute(id);

    verify(repository, times(1)).deleteById(id);
  }

  @Test
  void shouldDoNothingWhenUserDoesNotExist() {
    final var id = UUID.randomUUID();

    when(repository.existsById(id)).thenReturn(false);

    useCase.execute(id);

    verify(repository, never()).deleteById(id);
  }
}
