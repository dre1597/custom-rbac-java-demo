package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.apps.user.domain.enums.*;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.common.domain.helpers.SearchQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.data.jpa.domain.Specification.where;

@ExtendWith(MockitoExtension.class)
class ListUsersUseCaseTest {
  @Mock
  private UserJpaRepository repository;

  @InjectMocks
  private ListUsersUseCase useCase;

  @Test
  void shouldReturnPaginatedUsersWithoutSearchTerm() {
    final var searchQuery = new SearchQuery(0, 10, "", "name", "ASC");
    final var pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

    final var role = new RoleJpaEntity(
        UUID.randomUUID(),
        "any_name",
        "any_description",
        RoleStatus.ACTIVE.name(),
        Instant.now(),
        Instant.now(),
        List.of(
            new PermissionJpaEntity(
                UUID.randomUUID(),
                PermissionName.READ.name(),
                PermissionScope.USER.name(),
                "any_description",
                PermissionStatus.ACTIVE.name(),
                Instant.now(),
                Instant.now()
            )
        )
    );

    final var userEntities = List.of(
        new UserJpaEntity(
            UUID.randomUUID(),
            "any_name",
            "any_description",
            RoleStatus.ACTIVE.name(),
            Instant.now(),
            Instant.now(),
            role
        ),
        new UserJpaEntity(
            UUID.randomUUID(),
            "other_name",
            "any_description",
            RoleStatus.ACTIVE.name(),
            Instant.now(),
            Instant.now(),
            role
        )
    );

    final var page = new PageImpl<>(userEntities, pageRequest, userEntities.size());

    when(repository.findAll(where(null), pageRequest)).thenReturn(page);

    final var result = useCase.execute(searchQuery);

    assertEquals(0, result.currentPage());
    assertEquals(10, result.perPage());
    assertEquals(userEntities.size(), result.total());

    final var firstUser = result.items().getFirst();
    assertEquals("any_name", firstUser.name());
    assertEquals(UserStatus.ACTIVE.name(), firstUser.status());
    assertEquals(role.getId(), firstUser.roleId());

    final var secondUser = result.items().get(1);
    assertEquals("other_name", secondUser.name());
    assertEquals(UserStatus.ACTIVE.name(), secondUser.status());
    assertEquals(role.getId(), secondUser.roleId());
  }

  @Test
  void shouldReturnEmptyPaginatedRolesWhenNoResults() {
    final var searchQuery = new SearchQuery(0, 10, "nonexistent", "name", "ASC");
    final var pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
    final var page = new PageImpl<>(List.of(), pageRequest, 0);

    when(repository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(page);

    final var result = useCase.execute(searchQuery);

    assertEquals(0, result.currentPage());
    assertEquals(10, result.perPage());
    assertEquals(0, result.total());
    assertTrue(result.items().isEmpty());
  }


  @Test
  void shouldSearchByNameWhenTermsMatchName() {
    final var searchQuery = new SearchQuery(0, 10, "", "name", "ASC");
    final var pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

    final var role = new RoleJpaEntity(
        UUID.randomUUID(),
        "any_name",
        "any_description",
        RoleStatus.ACTIVE.name(),
        Instant.now(),
        Instant.now(),
        List.of(
            new PermissionJpaEntity(
                UUID.randomUUID(),
                PermissionName.READ.name(),
                PermissionScope.USER.name(),
                "any_description",
                PermissionStatus.ACTIVE.name(),
                Instant.now(),
                Instant.now()
            )
        )
    );

    final var userEntities = List.of(
        new UserJpaEntity(
            UUID.randomUUID(),
            "any_name",
            "any_description",
            RoleStatus.ACTIVE.name(),
            Instant.now(),
            Instant.now(),
            role
        )
    );

    final var page = new PageImpl<>(userEntities, pageRequest, userEntities.size());

    when(repository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(page);

    final var result = useCase.execute(searchQuery);

    assertEquals(1, result.items().size());
    assertEquals("any_name", result.items().getFirst().name());
  }
}
