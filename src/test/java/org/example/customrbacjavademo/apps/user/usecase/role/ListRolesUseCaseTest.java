package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionStatus;
import org.example.customrbacjavademo.apps.user.domain.enums.RoleStatus;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
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
class ListRolesUseCaseTest {
  @Mock
  private RoleJpaRepository repository;

  @InjectMocks
  private ListRolesUseCase useCase;

  @Test
  void shouldReturnPaginatedRolesWithoutSearchTerm() {
    final var searchQuery = new SearchQuery(0, 10, "", "name", "ASC");
    final var pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

    final var permissions = List.of(
        new PermissionJpaEntity(
            UUID.randomUUID(),
            PermissionName.READ.name(),
            PermissionScope.USER.name(),
            "any_description",
            PermissionStatus.ACTIVE.name(),
            Instant.now(),
            Instant.now()
        )
    );

    final var roleEntities = List.of(
        new RoleJpaEntity(
            UUID.randomUUID(),
            "any_name",
            "any_description",
            RoleStatus.ACTIVE.name(),
            Instant.now(),
            Instant.now(),
            permissions
        ),
        new RoleJpaEntity(
            UUID.randomUUID(),
            "other_name",
            "other_description",
            RoleStatus.ACTIVE.name(),
            Instant.now(),
            Instant.now(),
            permissions
        )
    );

    final var page = new PageImpl<>(roleEntities, pageRequest, roleEntities.size());

    when(repository.findAll(where(null), pageRequest)).thenReturn(page);

    final var result = useCase.execute(searchQuery);

    assertEquals(0, result.currentPage());
    assertEquals(10, result.perPage());
    assertEquals(2, result.total());
    assertEquals(2, result.items().size());

    final var firstRole = result.items().getFirst();
    assertEquals("any_name", firstRole.name());
    assertEquals("any_description", firstRole.description());
    assertEquals(RoleStatus.ACTIVE.name(), firstRole.status());

    final var secondRole = result.items().get(1);
    assertEquals("other_name", secondRole.name());
    assertEquals("other_description", secondRole.description());
    assertEquals(RoleStatus.ACTIVE.name(), secondRole.status());
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
    final var searchQuery = new SearchQuery(0, 10, "name", "name", "ASC");
    final var pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

    final var permissions = List.of(
        new PermissionJpaEntity(
            UUID.randomUUID(),
            PermissionName.READ.name(),
            PermissionScope.USER.name(),
            "any_description",
            PermissionStatus.ACTIVE.name(),
            Instant.now(),
            Instant.now()
        )
    );

    final var roleEntities = List.of(
        new RoleJpaEntity(
            UUID.randomUUID(),
            "any_name",
            "any_description",
            RoleStatus.ACTIVE.name(),
            Instant.now(),
            Instant.now(),
            List.of(permissions.getFirst())
        )
    );

    final var page = new PageImpl<>(roleEntities, pageRequest, roleEntities.size());

    when(repository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(page);

    final var result = useCase.execute(searchQuery);

    assertEquals(1, result.items().size());
    assertEquals("any_name", result.items().getFirst().name());
  }

  @Test
  void shouldSearchByDescriptionWhenTermsMatchDescription() {
    final var searchQuery = new SearchQuery(0, 10, "any_description", "name", "ASC");
    final var pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

    final var permissions = List.of(
        new PermissionJpaEntity(
            UUID.randomUUID(),
            PermissionName.READ.name(),
            PermissionScope.USER.name(),
            "any_description",
            PermissionStatus.ACTIVE.name(),
            Instant.now(),
            Instant.now()
        )
    );

    final var roleEntities = List.of(
        new RoleJpaEntity(
            UUID.randomUUID(),
            "any_name",
            "any_description",
            RoleStatus.ACTIVE.name(),
            Instant.now(),
            Instant.now(),
            List.of(permissions.getFirst())
        )
    );

    final var page = new PageImpl<>(roleEntities, pageRequest, roleEntities.size());

    when(repository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(page);

    final var result = useCase.execute(searchQuery);

    assertEquals(1, result.items().size());
    assertEquals("any_description", result.items().getFirst().description());
  }
}
