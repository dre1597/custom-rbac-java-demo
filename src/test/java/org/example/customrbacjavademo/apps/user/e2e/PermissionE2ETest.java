package org.example.customrbacjavademo.apps.user.e2e;

import org.example.customrbacjavademo.E2ETest;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
          "description": "some description"
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldNotCreatePermissionWithTheSameNameAndScopeTogether() throws Exception {
    final var firstPermission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    final var json = """
        {
          "name": "%s",
          "scope": "%s",
          "status": "ACTIVE",
          "description": "some description"
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
          "description": "some description"
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
          "description": "some description"
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }

  @Test
  void shouldNotCreatePermissionWithEmptyName() throws Exception {
    final var json = """
        {
          "name": "",
          "scope": "USER",
          "status": "ACTIVE",
          "description": "some description"
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name must be one of CREATE, READ, UPDATE, DELETE"));
  }

  @Test
  void shouldNotCreatePermissionWithBlankName() throws Exception {
    final var json = """
        {
          "name": "  ",
          "scope": "USER",
          "status": "ACTIVE",
          "description": "some description"
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name must be one of CREATE, READ, UPDATE, DELETE"));
  }

  @Test
  void shouldNotCreatePermissionWithInvalidName() throws Exception {
    final var json = """
        {
          "name": "INVALID",
          "scope": "USER",
          "status": "ACTIVE",
          "description": "some description"
        }
        """;

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
          "description": "some description"
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
          "description": "some description"
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("scope is required"));
  }

  @Test
  void shouldNotCreatePermissionWithEmptyScope() throws Exception {
    final var json = """
        {
          "name": "READ",
          "scope": "",
          "status": "ACTIVE",
          "description": "some description"
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("scope must be one of USER, PROFILE, ROLE, PERMISSION"));
  }

  @Test
  void shouldNotCreatePermissionWithBlankScope() throws Exception {
    final var json = """
        {
          "name": "READ",
          "scope": "  ",
          "status": "ACTIVE",
          "description": "some description"
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("scope must be one of USER, PROFILE, ROLE, PERMISSION"));
  }

  @Test
  void shouldNotCreatePermissionWithInvalidScope() throws Exception {
    final var json = """
        {
          "name": "READ",
          "scope": "INVALID",
          "status": "ACTIVE",
          "description": "some description"
        }
        """;

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
          "description": "some description"
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status is required"));
  }

  @Test
  void shouldNotCreatePermissionWithNullStatus() throws Exception {
    final var json = """
        {
          "name": "READ",
          "scope": "USER",
          "status": null,
          "description": "some description"
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status is required"));
  }

  @Test
  void shouldNotCreatePermissionWithEmptyStatus() throws Exception {
    final var json = """
        {
          "name": "READ",
          "scope": "USER",
          "status": "",
          "description": "some description"
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status must be one of ACTIVE, INACTIVE"));
  }

  @Test
  void shouldNotCreatePermissionWithBlankStatus() throws Exception {
    final var json = """
        {
          "name": "READ",
          "scope": "USER",
          "status": "  ",
          "description": "some description"
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status must be one of ACTIVE, INACTIVE"));
  }

  @Test
  void shouldNotCreatePermissionWithInvalidStatus() throws Exception {
    final var json = """
        {
          "name": "READ",
          "scope": "USER",
          "status": "INVALID",
          "description": "some description"
        }
        """;

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

  @Test
  void shouldNotCreatePermissionWithEmptyDescription() throws Exception {
    final var json = """
        {
          "name": "READ",
          "scope": "USER",
          "status": "ACTIVE",
          "description": ""
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }

  @Test
  void shouldNotCreatePermissionWithBlankDescription() throws Exception {
    final var json = """
        {
          "name": "READ",
          "scope": "USER",
          "status": "ACTIVE",
          "description": "  "
        }
        """;

    mvc.perform(post("/permissions")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }
}
