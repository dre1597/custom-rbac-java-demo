package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.IntegrationTest;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.RoleTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;
import org.example.customrbacjavademo.common.domain.helpers.SearchQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
class ListRolesUseCaseIntegrationTest {
  @Autowired
  private ListRolesUseCase useCase;

  @Autowired
  private RoleJpaRepository repository;

  @Autowired
  private PermissionJpaRepository permissionRepository;

  @Test
  void shouldReturnPaginatedRolesWithoutSearchTerm() {
    final var permission = PermissionTestMocks.createActiveTestPermission();
    final var permissionJpa = permissionRepository.save(PermissionMapper.entityToJpa(permission));
    final var firstRole = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permissionJpa.getId()))));
    final var secondRole = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole("other_role", List.of(permissionJpa.getId()))));

    final var searchQuery = new SearchQuery(0, 10, "", "name", "ASC");

    final var result = useCase.execute(searchQuery);

    assertEquals(0, result.currentPage());
    assertEquals(10, result.perPage());
    assertEquals(2, result.total());
    assertEquals(2, result.items().size());

    final var firstRoleResult = result.items().getFirst();
    assertEquals(firstRole.getId().toString(), firstRoleResult.id());
    assertEquals(firstRole.getName(), firstRoleResult.name());
    assertEquals(firstRole.getDescription(), firstRoleResult.description());
    assertEquals(firstRole.getStatus(), firstRoleResult.status());

    final var secondRoleResult = result.items().get(1);
    assertEquals(secondRole.getId().toString(), secondRoleResult.id());
    assertEquals(secondRole.getName(), secondRoleResult.name());
    assertEquals(secondRole.getDescription(), secondRoleResult.description());
    assertEquals(secondRole.getStatus(), secondRoleResult.status());
  }

  @Test
  void shouldReturnPaginatedPermissionsWithSearchTerms() {
    final var permission = PermissionTestMocks.createActiveTestPermission();
    final var permissionJpa = permissionRepository.save(PermissionMapper.entityToJpa(permission));
    repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permissionJpa.getId()))));
    final var otherRole = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole("other_role", List.of(permissionJpa.getId()))));

    final var searchQuery = new SearchQuery(0, 10, "other", "name", "ASC");

    final var result = useCase.execute(searchQuery);

    assertEquals(0, result.currentPage());
    assertEquals(10, result.perPage());
    assertEquals(1, result.total());
    assertEquals(1, result.items().size());

    final var firstRoleResult = result.items().getFirst();
    assertEquals(otherRole.getId().toString(), firstRoleResult.id());
    assertEquals(otherRole.getName(), firstRoleResult.name());
    assertEquals(otherRole.getDescription(), firstRoleResult.description());
    assertEquals(otherRole.getStatus(), firstRoleResult.status());
  }

  @Test
  void shouldReturnEmptyPaginatedRolesWhenNoResults() {
    final var searchQuery = new SearchQuery(0, 10, "other", "name", "ASC");

    final var result = useCase.execute(searchQuery);

    assertEquals(0, result.currentPage());
    assertEquals(10, result.perPage());
    assertEquals(0, result.total());
    assertTrue(result.items().isEmpty());
  }

  @Test
  void shouldSearchByDescriptionWhenTermsMatchDescription() {
    final var permission = PermissionTestMocks.createActiveTestPermission();
    final var permissionJpa = permissionRepository.save(PermissionMapper.entityToJpa(permission));
    repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permissionJpa.getId()))));
    final var otherRole = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole("any_name", "other_description", List.of(permissionJpa.getId()))));

    final var searchQuery = new SearchQuery(0, 10, "other", "description", "ASC");

    final var result = useCase.execute(searchQuery);

    assertEquals(0, result.currentPage());
    assertEquals(10, result.perPage());
    assertEquals(1, result.total());
    assertEquals(1, result.items().size());

    final var firstRoleResult = result.items().getFirst();
    assertEquals(otherRole.getId().toString(), firstRoleResult.id());
    assertEquals(otherRole.getName(), firstRoleResult.name());
    assertEquals(otherRole.getDescription(), firstRoleResult.description());
    assertEquals(otherRole.getStatus(), firstRoleResult.status());
  }
}
