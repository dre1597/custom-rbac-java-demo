package org.example.customrbacjavademo.apps.user.usecase.permission;

import org.example.customrbacjavademo.IntegrationTest;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.common.domain.helpers.SearchQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
class ListPermissionsUseCaseIntegrationTest {
  @Autowired
  private ListPermissionsUseCase useCase;

  @Autowired
  private PermissionJpaRepository repository;

  @Test
  void shouldReturnPaginatedPermissionsWithoutSearchTerm() {
    final var firstPermissionSaved = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var secondPermissionSaved = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    final var searchQuery = new SearchQuery(0, 10, "", "name", "asc");

    final var result = useCase.execute(searchQuery);

    assertEquals(0, result.currentPage());
    assertEquals(10, result.perPage());
    assertEquals(2, result.total());
    assertEquals(2, result.items().size());

    final var firstPermissionResult = result.items().getFirst();
    assertEquals(firstPermissionSaved.getName(), firstPermissionResult.name());
    assertEquals(firstPermissionSaved.getScope(), firstPermissionResult.scope());
    assertEquals(firstPermissionSaved.getDescription(), firstPermissionResult.description());
    assertEquals(firstPermissionSaved.getStatus(), firstPermissionResult.status());

    final var secondPermissionResult = result.items().get(1);
    assertEquals(secondPermissionSaved.getName(), secondPermissionResult.name());
    assertEquals(secondPermissionSaved.getScope(), secondPermissionResult.scope());
    assertEquals(secondPermissionSaved.getDescription(), secondPermissionResult.description());
    assertEquals(secondPermissionSaved.getStatus(), secondPermissionResult.status());
  }

  @Test
  void shouldReturnPaginatedPermissionsWithSearchTerms() {
    final var permissionSaved = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission(PermissionName.UPDATE.name())));

    final var searchQuery = new SearchQuery(0, 10, "read", "name", "ASC");

    final var result = useCase.execute(searchQuery);

    assertEquals(0, result.currentPage());
    assertEquals(10, result.perPage());
    assertEquals(1, result.total());
    assertEquals(1, result.items().size());

    final var permission = result.items().getFirst();
    assertEquals(permissionSaved.getName(), permission.name());
    assertEquals(permissionSaved.getScope(), permission.scope());
    assertEquals(permissionSaved.getDescription(), permission.description());
    assertEquals(permissionSaved.getStatus(), permission.status());
  }

  @Test
  void shouldReturnEmptyPaginatedPermissionsWhenNoResults() {
    final var searchQuery = new SearchQuery(0, 10, "nonexistent", "name", "ASC");

    final var result = useCase.execute(searchQuery);

    assertEquals(0, result.currentPage());
    assertEquals(10, result.perPage());
    assertEquals(0, result.total());
    assertTrue(result.items().isEmpty());
  }

  @Test
  void shouldSearchByScopeWhenTermsMatchScope() {
    final var permissionSaved = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    final var searchQuery = new SearchQuery(0, 10, permissionSaved.getScope(), "name", "ASC");

    final var result = useCase.execute(searchQuery);

    assertEquals(0, result.currentPage());
    assertEquals(10, result.perPage());
    assertEquals(1, result.total());
    assertEquals(1, result.items().size());

    final var firstPermissionResult = result.items().getFirst();
    assertEquals(permissionSaved.getName(), firstPermissionResult.name());
    assertEquals(permissionSaved.getScope(), firstPermissionResult.scope());
    assertEquals(permissionSaved.getDescription(), firstPermissionResult.description());
    assertEquals(permissionSaved.getStatus(), firstPermissionResult.status());
  }

  @Test
  void shouldSearchByDescriptionWhenTermsMatchDescription() {
    final var permissionSaved = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    var searchQuery = new SearchQuery(0, 10, permissionSaved.getDescription(), "name", "ASC");

    final var result = useCase.execute(searchQuery);

    assertEquals(0, result.currentPage());
    assertEquals(10, result.perPage());
    assertEquals(1, result.total());
    assertEquals(1, result.items().size());

    final var firstPermissionResult = result.items().getFirst();
    assertEquals(permissionSaved.getName(), firstPermissionResult.name());
    assertEquals(permissionSaved.getScope(), firstPermissionResult.scope());
    assertEquals(permissionSaved.getDescription(), firstPermissionResult.description());
    assertEquals(permissionSaved.getStatus(), firstPermissionResult.status());
  }
}
