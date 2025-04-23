package org.example.customrbacjavademo.apps.user.e2e;

import org.example.customrbacjavademo.E2ETest;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.RoleTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
class RoleE2ETest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  private RoleJpaRepository repository;

  @Autowired
  private PermissionJpaRepository permissionRepository;

  @Test
  void shouldListRolesWithoutSearchTerm() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var permissionsIds = List.of(permission.getId());
    final var firstRole = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(permissionsIds)));
    final var secondRole = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole("other_role", permissionsIds)));

    mvc.perform(get("/roles"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currentPage").value(0))
        .andExpect(jsonPath("$.perPage").value(10))
        .andExpect(jsonPath("$.total").value(2))
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items", hasSize(2)))
        .andExpect(jsonPath("$.items[*]", containsInAnyOrder(
            allOf(
                hasEntry("id", firstRole.getId().toString()),
                hasEntry("name", firstRole.getName()),
                hasEntry("description", firstRole.getDescription()),
                hasEntry("status", firstRole.getStatus())
            ),
            allOf(
                hasEntry("id", secondRole.getId().toString()),
                hasEntry("name", secondRole.getName()),
                hasEntry("description", secondRole.getDescription()),
                hasEntry("status", secondRole.getStatus())
            )
        )))
        .andExpect(jsonPath("$.items[0].createdAt").exists())
        .andExpect(jsonPath("$.items[0].updatedAt").exists())
        .andExpect(jsonPath("$.items[1].createdAt").exists())
        .andExpect(jsonPath("$.items[1].updatedAt").exists());
  }

  @Test
  void shouldListRolesWithSearchTerm() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var permissionsIds = List.of(permission.getId());
    repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(permissionsIds)));
    final var secondRole = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole("other_role", permissionsIds)));

    mvc.perform(get("/roles?search=other"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currentPage").value(0))
        .andExpect(jsonPath("$.perPage").value(10))
        .andExpect(jsonPath("$.total").value(1))
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items", hasSize(1)))
        .andExpect(jsonPath("$.items[*]", containsInAnyOrder(
            allOf(
                hasEntry("id", secondRole.getId().toString()),
                hasEntry("name", secondRole.getName()),
                hasEntry("description", secondRole.getDescription()),
                hasEntry("status", secondRole.getStatus())
            )
        )))
        .andExpect(jsonPath("$.items[0].createdAt").exists())
        .andExpect(jsonPath("$.items[0].updatedAt").exists());
  }

  @Test
  void shouldReturnEmptyPaginatedRolesWhenNoResults() throws Exception {
    mvc.perform(get("/roles?search=nonexistent"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currentPage").value(0))
        .andExpect(jsonPath("$.perPage").value(10))
        .andExpect(jsonPath("$.total").value(0))
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items", hasSize(0)));
  }

  @Test
  void shouldCreateRole() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    final var json = """
        {
          "name": "any_role",
          "description": "any_description",
          "status": "ACTIVE",
          "permissions": ["%s"]
        }
        """.formatted(permission.getId());

    final var result = mvc.perform(post("/roles")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isCreated());

    final var response = result.andReturn().getResponse();
    assertThat(response.getHeader("Location")).isNotNull();

    final var roles = repository.findAll();
    final var firstRole = roles.getFirst();
    assertThat(roles).hasSize(1);
    assertThat(firstRole.getName()).isEqualTo("any_role");
    assertThat(firstRole.getDescription()).isEqualTo("any_description");
    assertThat(firstRole.getStatus()).isEqualTo("ACTIVE");
  }

  @Test
  void shouldNotCreateRoleWithTheSameName() throws Exception {
    final var permissions = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permissions.getId()))));

    final var json = """
        {
          "name": "%s",
          "description": "any_description",
          "status": "ACTIVE",
          "permissions": ["%s"]
        }
        """.formatted(role.getName(), permissions.getId());

    mvc.perform(post("/roles")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Role already exists"));
  }

  @Test
  void shouldNotCreateRoleWithoutName() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    final var json = """
        {
          "description": "any_description",
          "status": "ACTIVE",
          "permissions": ["%s"]
        }
        """.formatted(permission.getId());

    mvc.perform(post("/roles")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }

  @Test
  void shouldNotCreateRoleWithNullName() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    final var json = """
        {
          "name": null,
          "description": "any_description",
          "status": "ACTIVE",
          "permissions": ["%s"]
        }
        """.formatted(permission.getId());

    mvc.perform(post("/roles")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '"})
  void shouldNotCreateRoleWithInvalidName(final String name) throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    final var json = """
        {
          "name": "%s",
          "description": "any_description",
          "status": "ACTIVE",
          "permissions": ["%s"]
        }
        """.formatted(name, permission.getId());

    mvc.perform(post("/roles")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }

  @Test
  void shouldNotCreateRoleWithoutDescription() throws Exception {
    final var permissions = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    final var json = """
        {
          "name": "any_role",
          "status": "ACTIVE",
          "permissions": ["%s"]
        }
        """.formatted(permissions.getId());

    mvc.perform(post("/roles")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }

  @Test
  void shouldNotCreateRoleWithNullDescription() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    final var json = """
        {
          "name": "any_role",
          "description": null,
          "status": "ACTIVE",
          "permissions": ["%s"]
        }
        """.formatted(permission.getId());

    mvc.perform(post("/roles")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '"})
  void shouldNotCreateRoleWithInvalidDescription(final String description) throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    final var json = """
        {
          "name": "any_role",
          "description": "%s",
          "status": "ACTIVE",
          "permissions": ["%s"]
        }
        """.formatted(description, permission.getId());

    mvc.perform(post("/roles")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }

  @Test
  void shouldNotCreateRoleWithoutStatus() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    final var json = """
        {
          "name": "any_role",
          "description": "any_description",
          "permissions": ["%s"]
        }
        """.formatted(permission.getId());

    mvc.perform(post("/roles")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status is required"));
  }

  @Test
  void shouldNotCreateRoleWithNullStatus() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    final var json = """
        {
          "name": "any_role",
          "description": "any_description",
          "status": null,
          "permissions": ["%s"]
        }
        """.formatted(permission.getId());

    mvc.perform(post("/roles")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '"})
  void shouldNotCreateRoleWithInvalidStatus(final String status) throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    final var json = """
        {
          "name": "any_role",
          "description": "any_description",
          "status": "%s",
          "permissions": ["%s"]
        }
        """.formatted(status, permission.getId());

    mvc.perform(post("/roles")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status must be one of ACTIVE, INACTIVE"));
  }

  @Test
  void shouldNotCreateRoleWithoutPermissions() throws Exception {
    final var json = """
        {
          "name": "any_role",
          "description": "any_description",
          "status": "ACTIVE"
        }
        """;

    mvc.perform(post("/roles")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("At least one permission must be provided"));
  }

  @Test
  void shouldGetRoleById() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));

    mvc.perform(get("/roles/{id}", role.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(role.getName()))
        .andExpect(jsonPath("$.description").value(role.getDescription()))
        .andExpect(jsonPath("$.status").value(role.getStatus()))
        .andExpect(jsonPath("$.permissions").isArray())
        .andExpect(jsonPath("$.permissions", hasSize(1)))
        .andExpect(jsonPath("$.permissions[*]", containsInAnyOrder(
            allOf(
                hasEntry("id", permission.getId().toString()),
                hasEntry("name", permission.getName()),
                hasEntry("scope", permission.getScope()),
                hasEntry("description", permission.getDescription()),
                hasEntry("status", permission.getStatus())
            )
        )))
        .andExpect(jsonPath("$.permissions[0].createdAt").exists())
        .andExpect(jsonPath("$.permissions[0].updatedAt").exists());
  }

  @Test
  void shouldNotGetRoleByIdWithNotFoundId() throws Exception {
    final var id = UUID.randomUUID().toString();
    mvc.perform(get("/roles/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Role not found"));
  }

  @Test
  void shouldNotGetRoleByIdWithInvalidId() throws Exception {
    final var id = "invalid_id";
    mvc.perform(get("/roles/{id}", id))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("Invalid UUID: " + id));
  }

  @Test
  void shouldUpdateRole() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));

    final var json = """
        {
          "name": "updated_role",
          "description": "updated_description",
          "status": "INACTIVE",
          "permissions": ["%s"]
        }
        """.formatted(permission.getId());

    mvc.perform(put("/roles/{id}", role.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isOk());

    final var roles = repository.findAll();
    final var firstRole = roles.getFirst();
    assertThat(roles).hasSize(1);
    assertThat(firstRole.getName()).isEqualTo("updated_role");
    assertThat(firstRole.getDescription()).isEqualTo("updated_description");
    assertThat(firstRole.getStatus()).isEqualTo("INACTIVE");
  }

  @Test
  void shouldUpdatePermissions() throws Exception {
    final var oldPermission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var newPermission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission("CREATE")));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(oldPermission.getId()))));

    final var json = """
        {
          "name": "%s",
          "description": "%s",
          "status": "%s",
          "permissions": ["%s"]
        }
        """.formatted(role.getName(), role.getDescription(), role.getStatus(), newPermission.getId());

    mvc.perform(put("/roles/{id}", role.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isOk());

    final var roleOnDatabase = repository.findWithPermissionsById(role.getId()).get();
    assertThat(roleOnDatabase.getPermissions()).hasSize(1);
    assertThat(roleOnDatabase.getPermissions().getFirst().getId()).isEqualTo(newPermission.getId());
  }

  @Test
  void shouldNotUpdateRoleToADuplicateName() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var roleToUpdate = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole("other_role", List.of(permission.getId()))));
    final var json = """
        {
          "name": "%s",
          "description": "%s",
          "status": "%s",
          "permissions": ["%s"]
        }
        """.formatted(role.getName(), roleToUpdate.getDescription(), roleToUpdate.getStatus(), permission.getId());

    mvc.perform(put("/roles/{id}", roleToUpdate.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Role already exists"));
  }

  @Test
  void shouldNotUpdateRoleWithoutName() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var json = """
        {
          "description": "%s",
          "status": "%s",
          "permissions": ["%s"]
        }
        """.formatted(role.getDescription(), role.getStatus(), permission.getId());

    mvc.perform(put("/roles/{id}", role.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }

  @Test
  void shouldNotUpdateRoleWithNullName() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var json = """
        {
          "name": null,
          "description": "%s",
          "status": "%s",
          "permissions": ["%s"]
        }
        """.formatted(role.getDescription(), role.getStatus(), permission.getId());

    mvc.perform(put("/roles/{id}", role.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '"})
  void shouldNotUpdateRoleWithInvalidName(final String name) throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var json = """
        {
          "name": "%s",
          "description": "%s",
          "status": "%s",
          "permissions": ["%s"]
        }
        """.formatted(name, role.getDescription(), role.getStatus(), permission.getId());

    mvc.perform(put("/roles/{id}", role.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }

  @Test
  void shouldNotUpdateRoleWithoutDescription() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var json = """
        {
          "name": "%s",
          "status": "%s",
          "permissions": ["%s"]
        }
        """.formatted(role.getName(), role.getStatus(), permission.getId());

    mvc.perform(put("/roles/{id}", role.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }

  @Test
  void shouldNotUpdateRoleWithNullDescription() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var json = """
        {
          "name": "%s",
          "description": null,
          "status": "%s",
          "permissions": ["%s"]
        }
        """.formatted(role.getName(), role.getStatus(), permission.getId());

    mvc.perform(put("/roles/{id}", role.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '"})
  void shouldNotUpdateRoleWithInvalidDescription(final String description) throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var json = """
        {
          "name": "%s",
          "description": "%s",
          "status": "%s",
          "permissions": ["%s"]
        }
        """.formatted(role.getName(), description, role.getStatus(), permission.getId());

    mvc.perform(put("/roles/{id}", role.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }

  @Test
  void shouldNotUpdateRoleWithoutStatus() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var json = """
        {
          "name": "%s",
          "description": "%s",
          "permissions": ["%s"]
        }
        """.formatted(role.getName(), role.getDescription(), permission.getId());

    mvc.perform(put("/roles/{id}", role.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status is required"));
  }

  @Test
  void shouldNotUpdateRoleWithNullStatus() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var json = """
        {
          "name": "%s",
          "description": "%s",
          "status": null,
          "permissions": ["%s"]
        }
        """.formatted(role.getName(), role.getDescription(), permission.getId());

    mvc.perform(put("/roles/{id}", role.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '"})
  void shouldNotUpdateRoleWithInvalidStatus() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var json = """
        {
          "name": "%s",
          "description": "%s",
          "status": "%s",
          "permissions": ["%s"]
        }
        """.formatted(role.getName(), role.getDescription(), "invalid_status", permission.getId());

    mvc.perform(put("/roles/{id}", role.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status must be one of ACTIVE, INACTIVE"));
  }

  @Test
  void shouldDeleteRole() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));

    mvc.perform(delete("/roles/{id}", role.getId())
            .contentType("application/json"))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldDoNothingWhenDeleteRoleWithNotFoundId() throws Exception {
    final var id = UUID.randomUUID().toString();
    mvc.perform(delete("/roles/{id}", id)
            .contentType("application/json"))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldNotDeleteRoleWithInvalidId() throws Exception {
    final var id = "invalid_id";
    mvc.perform(delete("/roles/{id}", id)
            .contentType("application/json"))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("Invalid UUID: " + id));
  }
}
