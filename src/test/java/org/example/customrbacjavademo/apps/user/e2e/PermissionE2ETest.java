package org.example.customrbacjavademo.apps.user.e2e;

import org.example.customrbacjavademo.E2ETest;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
class PermissionE2ETest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  private PermissionJpaRepository repository;

  @Test
  void shouldListPermissionsWithoutSearchTerm() throws Exception {
    final var firstPermission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var secondPermission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    mvc.perform(get("/permissions"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currentPage").value(0))
        .andExpect(jsonPath("$.perPage").value(10))
        .andExpect(jsonPath("$.total").value(2))
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items", hasSize(2)))
        .andExpect(jsonPath("$.items[*]", containsInAnyOrder(
            allOf(
                hasEntry("id", firstPermission.getId().toString()),
                hasEntry("name", firstPermission.getName()),
                hasEntry("scope", firstPermission.getScope()),
                hasEntry("description", firstPermission.getDescription()),
                hasEntry("status", firstPermission.getStatus())
            ),
            allOf(
                hasEntry("id", secondPermission.getId().toString()),
                hasEntry("name", secondPermission.getName()),
                hasEntry("scope", secondPermission.getScope()),
                hasEntry("description", secondPermission.getDescription()),
                hasEntry("status", secondPermission.getStatus())
            )
        )));
  }

  @Test
  void shouldListPermissionsWithSearchTerm() throws Exception {
    final var firstPermission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission(PermissionName.UPDATE.name())));

    mvc.perform(get("/permissions?search=read"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currentPage").value(0))
        .andExpect(jsonPath("$.perPage").value(10))
        .andExpect(jsonPath("$.total").value(1))
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items", hasSize(1)))
        .andExpect(jsonPath("$.items[*]", containsInAnyOrder(
            allOf(
                hasEntry("id", firstPermission.getId().toString()),
                hasEntry("name", firstPermission.getName()),
                hasEntry("scope", firstPermission.getScope()),
                hasEntry("description", firstPermission.getDescription()),
                hasEntry("status", firstPermission.getStatus())
            )
        )));
  }

  @Test
  void shouldReturnEmptyPaginatedPermissionsWhenNoResults() throws Exception {
    mvc.perform(get("/permissions?search=nonexistent"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currentPage").value(0))
        .andExpect(jsonPath("$.perPage").value(10))
        .andExpect(jsonPath("$.total").value(0))
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items", hasSize(0)));
  }

  @Test
  void shouldCreatePermission() throws Exception {
    final var json = """
        {
          "name": "READ",
          "scope": "USER",
          "status": "ACTIVE",
          "description": "any_description"
        }
        """;

    final var result = mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isCreated());

    final var response = result.andReturn().getResponse();
    assertThat(response.getHeader("Location")).isNotNull();

    final var permissions = repository.findAll();
    final var firstPermission = permissions.getFirst();
    assertThat(permissions).hasSize(1);
    assertThat(firstPermission.getName()).isEqualTo("READ");
    assertThat(firstPermission.getScope()).isEqualTo("USER");
    assertThat(firstPermission.getStatus()).isEqualTo("ACTIVE");
    assertThat(firstPermission.getDescription()).isEqualTo("any_description");
  }

  @Test
  void shouldNotCreatePermissionWithTheSameNameAndScopeTogether() throws Exception {
    final var firstPermission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var json = """
        {
          "name": "%s",
          "scope": "%s",
          "status": "ACTIVE",
          "description": "any_description"
        }
        """.formatted(
        firstPermission.getName(),
        firstPermission.getScope()
    );

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Permission already exists"));
  }

  @Test
  void shouldNotCreatePermissionWithoutName() throws Exception {
    final var json = """
        {
          "scope": "USER",
          "status": "ACTIVE",
          "description": "any_description"
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }

  @Test
  void shouldNotCreatePermissionWithNullName() throws Exception {
    final var json = """
        {
          "name": null,
          "scope": "USER",
          "status": "ACTIVE",
          "description": "any_description"
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '", "INVALID" })
  void shouldNotCreatePermissionWithInvalidName(final String name) throws Exception {
    final var json = """
        {
          "name": "%s",
          "scope": "USER",
          "status": "ACTIVE",
          "description": "any_description"
        }
        """.formatted(name);

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name must be one of CREATE, READ, UPDATE, DELETE"));
  }

  @Test
  void shouldNotCreatePermissionWithoutScope() throws Exception {
    final var json = """
        {
          "name": "READ",
          "status": "ACTIVE",
          "description": "any_description"
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("scope is required"));
  }

  @Test
  void shouldNotCreatePermissionWithNullScope() throws Exception {
    final var json = """
        {
          "name": "READ",
          "scope": null,
          "status": "ACTIVE",
          "description": "any_description"
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("scope is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '", "INVALID" })
  void shouldNotCreatePermissionWithInvalidScope(final String scope) throws Exception {
    final var json = """
        {
          "name": "READ",
          "scope": "%s",
          "status": "ACTIVE",
          "description": "any_description"
        }
        """.formatted(scope);

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("scope must be one of USER, PROFILE, ROLE, PERMISSION"));
  }

  @Test
  void shouldNotCreatePermissionWithoutStatus() throws Exception {
    final var json = """
        {
          "name": "READ",
          "scope": "USER",
          "description": "any_description"
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '", "INVALID" })
  void shouldNotCreatePermissionWithInvalidStatus(final String status) throws Exception {
    final var json = """
        {
          "name": "READ",
          "scope": "USER",
          "status": "%s",
          "description": "any_description"
        }
        """.formatted(status);

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status must be one of ACTIVE, INACTIVE"));
  }

  @Test
  void shouldNotCreatePermissionWithoutDescription() throws Exception {
    final var json = """
        {
          "name": "READ",
          "scope": "USER",
          "status": "ACTIVE"
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }

  @Test
  void shouldNotCreatePermissionWithNullDescription() throws Exception {
    final var json = """
        {
          "name": "READ",
          "scope": "USER",
          "status": "ACTIVE",
          "description": null
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '" })
  void shouldNotCreatePermissionWithInvalidDescription(final String description) throws Exception {
    final var json = """
        {
          "name": "READ",
          "scope": "USER",
          "status": "ACTIVE",
          "description": "%s"
        }
        """.formatted(description);

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }

  @Test
  void shouldGetPermissionById() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    mvc.perform(get("/permissions/{id}", permission.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(permission.getId().toString()))
        .andExpect(jsonPath("$.name").value(permission.getName()))
        .andExpect(jsonPath("$.scope").value(permission.getScope()))
        .andExpect(jsonPath("$.status").value(permission.getStatus()))
        .andExpect(jsonPath("$.description").value(permission.getDescription()));
  }

  @Test
  void shouldNotGetPermissionByIdWithNotFoundId() throws Exception {
    final var id = UUID.randomUUID().toString();
    mvc.perform(get("/permissions/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Permission not found"));
  }

  @Test
  void shouldNotGetPermissionByIdWithInvalidId() throws Exception {
    final var id = "invalid_id";
    mvc.perform(get("/permissions/{id}", id))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("Invalid UUID: " + id));
  }

  @Test
  void shouldUpdatePermission() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var json = """
        {
          "name": "CREATE",
          "scope": "PERMISSION",
          "status": "ACTIVE",
          "description": "updated_description"
        }
        """;


    mvc.perform(put("/permissions/{id}", permission.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isOk());

    final var permissions = repository.findAll();
    final var firstPermission = permissions.getFirst();
    assertThat(permissions).hasSize(1);
    assertThat(firstPermission.getName()).isEqualTo("CREATE");
    assertThat(firstPermission.getScope()).isEqualTo("PERMISSION");
    assertThat(firstPermission.getStatus()).isEqualTo("ACTIVE");
    assertThat(firstPermission.getDescription()).isEqualTo("updated_description");
  }

  @Test
  void shouldNotUpdatePermissionWithTheSameNameAndScopeTogether() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var permissionToUpdate = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission(PermissionName.UPDATE.name())));

    final var json = """
        {
          "name": "%s",
          "scope": "%s",
          "status": "ACTIVE",
          "description": "any_description"
        }
        """.formatted(
        permission.getName(),
        permission.getScope()
    );


    mvc.perform(put("/permissions/{id}", permissionToUpdate.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Permission already exists"));
  }

  @Test
  void shouldNotUpdatePermissionWithoutName() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var json = """
        {
          "scope": "PERMISSION",
          "status": "ACTIVE",
          "description": "any_description"
        }
        """;

    mvc.perform(put("/permissions/{id}", permission.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }

  @Test
  void shouldNotUpdatePermissionWitNullName() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var json = """
        {
          "name": null,
          "scope": "PERMISSION",
          "status": "ACTIVE",
          "description": "any_description"
        }
        """;

    mvc.perform(put("/permissions/{id}", permission.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }


  @ParameterizedTest
  @CsvSource(value = {"''", "'  '" })
  void shouldNotUpdatePermissionWithInvalidName(final String name) throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var json = """
        {
          "name": "%s",
          "scope": "PERMISSION",
          "status": "ACTIVE",
          "description": "any_description"
        }
        """.formatted(name);

    mvc.perform(put("/permissions/{id}", permission.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name must be one of CREATE, READ, UPDATE, DELETE"));
  }

  @Test
  void shouldNotUpdatePermissionWithoutScope() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var json = """
        {
          "name": "CREATE",
          "status": "ACTIVE",
          "description": "any_description"
        }
        """;

    mvc.perform(put("/permissions/{id}", permission.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("scope is required"));
  }

  @Test
  void shouldNotUpdatePermissionWithNullScope() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var json = """
        {
          "name": "CREATE",
          "scope": null,
          "status": "ACTIVE",
          "description": "any_description"
        }
        """;

    mvc.perform(put("/permissions/{id}", permission.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("scope is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '" })
  void shouldNotUpdatePermissionWithNullScope(final String scope) throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var json = """
        {
          "name": "CREATE",
          "scope": "%s",
          "status": "ACTIVE",
          "description": "any_description"
        }
        """.formatted(scope);

    mvc.perform(put("/permissions/{id}", permission.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("scope must be one of USER, PROFILE, ROLE, PERMISSION"));
  }

  @Test
  void shouldNotUpdatePermissionWithoutStatus() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var json = """
        {
          "name": "CREATE",
          "scope": "PERMISSION",
          "description": "any_description"
        }
        """;

    mvc.perform(put("/permissions/{id}", permission.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status is required"));
  }

  @Test
  void shouldNotUpdatePermissionWithNullStatus() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var json = """
        {
          "name": "CREATE",
          "scope": "PERMISSION",
          "status": null,
          "description": "any_description"
        }
        """;

    mvc.perform(put("/permissions/{id}", permission.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '" })
  void shouldNotUpdatePermissionWithInvalidStatus(final String status) throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var json = """
        {
          "name": "CREATE",
          "scope": "PERMISSION",
          "status": "%s",
          "description": "any_description"
        }
        """.formatted(status);

    mvc.perform(put("/permissions/{id}", permission.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status must be one of ACTIVE, INACTIVE"));
  }

  @Test
  void shouldNotUpdatePermissionWithoutDescription() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var json = """
        {
          "name": "CREATE",
          "scope": "PERMISSION",
          "status": "ACTIVE"
        }
        """;

    mvc.perform(put("/permissions/{id}", permission.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }

  @Test
  void shouldNotUpdatePermissionWithNullDescription() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var json = """
        {
          "name": "CREATE",
          "scope": "PERMISSION",
          "status": "ACTIVE",
          "description": null
        }
        """;

    mvc.perform(put("/permissions/{id}", permission.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '" })
  void shouldNotUpdatePermissionWithInvalidDescription(final String description) throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var json = """
        {
          "name": "CREATE",
          "scope": "PERMISSION",
          "status": "ACTIVE",
          "description": "%s"
        }
        """.formatted(description);

    mvc.perform(put("/permissions/{id}", permission.getId())
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }

  @Test
  void shouldDeletePermission() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    mvc.perform(delete("/permissions/{id}", permission.getId())
            .contentType("application/json"))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldDoNothingWhenDeletePermissionWithNotFoundId() throws Exception {
    final var id = UUID.randomUUID().toString();
    mvc.perform(delete("/permissions/{id}", id)
            .contentType("application/json"))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldNotDeletePermissionWhenIdIsNotAValidUUID() throws Exception {
    final var id = "invalid_id";
    mvc.perform(delete("/permissions/{id}", id)
            .contentType("application/json"))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("Invalid UUID: " + id));
  }
}
