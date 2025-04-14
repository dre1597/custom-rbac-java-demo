package org.example.customrbacjavademo.apps.user.usecase.permission;

import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletePermissionUseCaseTest {
  @Mock
  private PermissionJpaRepository repository;

  @InjectMocks
  private DeletePermissionUseCase useCase;

  @Test
  void shouldDeletePermission() {
    final var id = UUID.randomUUID();

    when(repository.existsById(id)).thenReturn(true);

    useCase.execute(id.toString());

    verify(repository, times(1)).deleteById(id);
  }

  @Test
  void shouldDoNothingWhenMangaDoesNotExist() {
    final var id = UUID.randomUUID();
    when(repository.existsById(id)).thenReturn(false);

    useCase.execute(id.toString());

    verify(repository, never()).delete(any());
  }

  @Test
  void shouldThrowValidationExceptionWhenIdIsNotAValidUUID() {
    final var id = "invalid_uuid";

    final var exception = assertThrows(ValidationException.class, () -> useCase.execute(id));

    assertEquals("Invalid UUID: invalid_uuid", exception.getMessage());
  }
}
