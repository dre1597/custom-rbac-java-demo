package org.example.customrbacjavademo.apps.user.usecase;

import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.mappers.PermissionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletePermissionUseCaseTest {
  @Mock
  private PermissionJpaRepository repository;

  @InjectMocks
  private DeletePermissionUseCase useCase;

  @Test
  void shouldDeletePermission() {
    var id = UUID.randomUUID();
    var permission = PermissionTestMocks.createActiveTestPermission();

    when(repository.findById(id))
        .thenReturn(Optional.of(PermissionMapper.entityToJpa(permission)));

    useCase.execute(id);

    verify(repository, times(1)).deleteById(id);
  }

  @Test
  void shouldDoNothingWhenMangaDoesNotExist() {
    var id = UUID.randomUUID();
    when(repository.findById(id)).thenReturn(Optional.empty());

    useCase.execute(id);

    verify(repository, never()).delete(any());
  }
}
