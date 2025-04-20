package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.apps.user.domain.mocks.UserTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetOneUserCaseTest {
  @Mock
  private UserJpaRepository repository;

  @InjectMocks
  private GetOneUserUseCase useCase;

  @Test
  void shouldGetUserById() {
    final var id = UUID.randomUUID();
    final var user = UserTestMocks.createActiveTestUserJpa();

    when(repository.findWithRoleById(id))
        .thenReturn(Optional.of(user));

    final var result = useCase.execute(id.toString());

    assertNotNull(result);
    assertEquals(user.getId().toString(), result.id());
    assertEquals(user.getName(), result.name());
    assertEquals(user.getRole().getId().toString(), result.role().id());
    assertEquals(user.getRole().getName(), result.role().name());
    assertEquals(user.getRole().getDescription(), result.role().description());
    assertEquals(user.getRole().getStatus(), result.role().status());
    assertEquals(user.getRole().getCreatedAt(), result.role().createdAt());
    assertEquals(user.getRole().getUpdatedAt(), result.role().updatedAt());
  }

  @Test
  void shouldThrowExceptionWhenUserNotFound() {
    final var id = UUID.randomUUID();

    when(repository.findWithRoleById(id))
        .thenReturn(Optional.empty());

    final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(id.toString()));

    assertEquals("User not found", exception.getMessage());
  }
}
