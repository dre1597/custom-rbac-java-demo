package org.example.customrbacjavademo.apps.user.usecase;

import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionStatus;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
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
class ListPermissionsUseCaseTest {
  @Mock
  private PermissionJpaRepository repository;

  @InjectMocks
  private ListPermissionsUseCase useCase;

  @Test
  void shouldReturnPaginatedPermissionsWithoutSearchTerm() {
    var searchQuery = new SearchQuery(0, 10, "", "name", "asc");
    var pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

    var permissionEntities = List.of(
        new PermissionJpaEntity(
            UUID.randomUUID(),
            PermissionName.READ.name(),
            PermissionScope.USER.name(),
            "any_description",
            PermissionStatus.ACTIVE.name(),
            Instant.now(),
            Instant.now()
        ),
        new PermissionJpaEntity(
            UUID.randomUUID(),
            PermissionName.CREATE.name(),
            PermissionScope.PROFILE.name(),
            "other_description",
            PermissionStatus.ACTIVE.name(),
            Instant.now(),
            Instant.now()
        )
    );

    var page = new PageImpl<>(permissionEntities, pageRequest, permissionEntities.size());

    when(repository.findAll(where(null), pageRequest)).thenReturn(page);

    var result = useCase.execute(searchQuery);

    assertEquals(0, result.currentPage());
    assertEquals(10, result.perPage());
    assertEquals(2, result.total());
    assertEquals(2, result.items().size());

    var firstPermission = result.items().getFirst();
    assertEquals(PermissionName.READ.name(), firstPermission.name());
    assertEquals(PermissionScope.USER.name(), firstPermission.scope());
    assertEquals("any_description", firstPermission.description());
    assertEquals(PermissionStatus.ACTIVE.name(), firstPermission.status());

    var secondPermission = result.items().get(1);
    assertEquals(PermissionName.CREATE.name(), secondPermission.name());
    assertEquals(PermissionScope.PROFILE.name(), secondPermission.scope());
    assertEquals("other_description", secondPermission.description());
    assertEquals(PermissionStatus.ACTIVE.name(), secondPermission.status());
  }

  @Test
  void shouldReturnPaginatedPermissionsWithSearchTerms() {
    var searchQuery = new SearchQuery(0, 10, "read", "name", "ASC");
    var pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

    var permissionEntities = List.of(
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

    var page = new PageImpl<>(permissionEntities, pageRequest, permissionEntities.size());

    when(repository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(page);

    var result = useCase.execute(searchQuery);

    assertEquals(0, result.currentPage());
    assertEquals(10, result.perPage());
    assertEquals(1, result.total());
    assertEquals(1, result.items().size());

    var permission = result.items().getFirst();
    assertEquals(PermissionName.READ.name(), permission.name());
    assertEquals(PermissionScope.USER.name(), permission.scope());
    assertEquals("any_description", permission.description());
    assertEquals(PermissionStatus.ACTIVE.name(), permission.status());
  }

  @Test
  void shouldReturnEmptyPaginatedPermissionsWhenNoResults() {
    var searchQuery = new SearchQuery(0, 10, "nonexistent", "name", "ASC");
    var pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

    var page = new PageImpl<>(List.of(), pageRequest, 0);

    when(repository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(page);

    var result = useCase.execute(searchQuery);

    assertEquals(0, result.currentPage());
    assertEquals(10, result.perPage());
    assertEquals(0, result.total());
    assertTrue(result.items().isEmpty());
  }

  @Test
  void shouldSearchByScopeWhenTermsMatchScope() {
    var searchQuery = new SearchQuery(0, 10, "user", "name", "ASC");
    var pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

    var permissionEntities = List.of(
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

    var page = new PageImpl<>(permissionEntities, pageRequest, permissionEntities.size());

    when(repository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(page);

    var result = useCase.execute(searchQuery);

    assertEquals(1, result.items().size());
    assertEquals(PermissionScope.USER.name(), result.items().getFirst().scope());
  }

  @Test
  void shouldSearchByDescriptionWhenTermsMatchDescription() {
    var searchQuery = new SearchQuery(0, 10, "any_description", "name", "ASC");
    var pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

    var permissionEntities = List.of(
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

    var page = new PageImpl<>(permissionEntities, pageRequest, permissionEntities.size());

    when(repository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(page);

    var result = useCase.execute(searchQuery);

    assertEquals(1, result.items().size());
    assertEquals("any_description", result.items().getFirst().description());
  }
}
