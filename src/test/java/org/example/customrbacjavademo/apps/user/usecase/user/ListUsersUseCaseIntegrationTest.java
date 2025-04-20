package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.IntegrationTest;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.RoleTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.UserTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;
import org.example.customrbacjavademo.common.domain.helpers.SearchQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
class ListUsersUseCaseIntegrationTest {
  @Autowired
  private ListUsersUseCase useCase;

  @Autowired
  private UserJpaRepository repository;

  @Autowired
  private RoleJpaRepository roleRepository;

  @Autowired
  private PermissionJpaRepository permissionRepository;

  @Test
  void shouldReturnPaginatedUsersWithoutSearchTerm() {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var firstUser = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));
    final var secondUser = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser("other_name", role.getId())));

    final var searchQuery = new SearchQuery(0, 10, "", "name", "ASC");

    final var result = useCase.execute(searchQuery);

    assertEquals(0, result.currentPage());
    assertEquals(10, result.perPage());
    assertEquals(2, result.total());
    assertEquals(2, result.items().size());

    final var firstUserResult = result.items().getFirst();
    assertEquals(firstUser.getId().toString(), firstUserResult.id());
    assertEquals(firstUser.getName(), firstUserResult.name());
    assertEquals(firstUser.getStatus(), firstUserResult.status());

    final var secondUserResult = result.items().get(1);
    assertEquals(secondUser.getId().toString(), secondUserResult.id());
    assertEquals(secondUser.getName(), secondUserResult.name());
    assertEquals(secondUser.getStatus(), secondUserResult.status());
  }

  @Test
  void shouldReturnPaginatedPermissionsWithSearchTerms() {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));
    final var secondUser = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser("other_name", role.getId())));

    final var searchQuery = new SearchQuery(0, 10, "other", "name", "ASC");

    final var result = useCase.execute(searchQuery);

    assertEquals(0, result.currentPage());
    assertEquals(10, result.perPage());
    assertEquals(1, result.total());
    assertEquals(1, result.items().size());

    final var firstUserResult = result.items().getFirst();
    assertEquals(secondUser.getId().toString(), firstUserResult.id());
    assertEquals(secondUser.getName(), firstUserResult.name());
    assertEquals(secondUser.getStatus(), firstUserResult.status());
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
}
