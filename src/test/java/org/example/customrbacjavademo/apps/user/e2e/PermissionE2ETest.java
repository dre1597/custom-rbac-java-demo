package org.example.customrbacjavademo.apps.user.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.customrbacjavademo.E2ETest;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionStatus;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.bootstrap.DefaultUserSeeder;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.common.domain.helpers.EnumUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

  @Autowired
  private DefaultUserSeeder seeder;

  @Value("${user.admin.name}")
  private String adminName;

  @Value("${user.admin.password}")
  private String adminPassword;

  private String authToken;

  @BeforeEach
  void setUp() throws Exception {
    seeder.seedAllManually();

    final var loginJson = """
        {
          "name": "%s",
          "password": "%s"
        }
        """.formatted(adminName, adminPassword);

    final var loginResponse = mvc.perform(post("/auth/login")
            .contentType("application/json")
            .content(loginJson))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    authToken = new ObjectMapper().readTree(loginResponse).get("token").asText();
  }

  @Test
  void shouldListPermissionsWithoutSearchTerm() throws Exception {
    mvc.perform(get("/permissions")
            .header("Authorization", "Bearer " + authToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currentPage").value(0))
        .andExpect(jsonPath("$.perPage").value(10))
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items[0].id").isString())
        .andExpect(jsonPath("$.items[0].name").isString())
        .andExpect(jsonPath("$.items[0].scope").isString())
        .andExpect(jsonPath("$.items[0].description").isString())
        .andExpect(jsonPath("$.items[0].status").isString());
  }

  @Test
  void shouldListPermissionsWithSearchTerm() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission(PermissionName.READ.name(), "any_description")));

    mvc.perform(get("/permissions?search=" + permission.getDescription())
            .header("Authorization", "Bearer " + authToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currentPage").value(0))
        .andExpect(jsonPath("$.perPage").value(10))
        .andExpect(jsonPath("$.total").value(1))
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items", hasSize(1)))
        .andExpect(jsonPath("$.items[*]", containsInAnyOrder(
            allOf(
                hasEntry("id", permission.getId().toString()),
                hasEntry("name", permission.getName()),
                hasEntry("scope", permission.getScope()),
                hasEntry("description", permission.getDescription()),
                hasEntry("status", permission.getStatus())
            )
        )));
  }

  @Test
  void shouldReturnEmptyPaginatedPermissionsWhenNoResults() throws Exception {
    mvc.perform(get("/permissions?search=nonexistent")
            .header("Authorization", "Bearer " + authToken))
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
          "name": "DELETE",
          "scope": "PROFILE",
          "status": "ACTIVE",
          "description": "any_description"
        }
        """;

    final var result = mvc.perform(post("/permissions")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isCreated());

    final var response = result.andReturn().getResponse();
    assertThat(response.getHeader("Location")).isNotNull();

    final var permissions = repository.existsByNameAndScope("DELETE", "PROFILE");
    assertThat(permissions).isTrue();
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
            .header("Authorization", "Bearer " + authToken)
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
            .header("Authorization", "Bearer " + authToken)
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
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '", "INVALID"})
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
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name must be one of " + EnumUtils.enumValuesAsString(PermissionName.class)));
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
            .header("Authorization", "Bearer " + authToken)
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
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("scope is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '", "INVALID"})
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
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("scope must be one of " + EnumUtils.enumValuesAsString(PermissionScope.class)));
  }

  @Test
  void shouldNotCreatePermissionWithoutStatus() throws Exception {
    final var json = """
        {
          "name": "DELETE",
          "scope": "PROFILE",
          "description": "any_description"
        }
        """;

    mvc.perform(post("/permissions")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '", "INVALID"})
  void shouldNotCreatePermissionWithInvalidStatus(final String status) throws Exception {
    final var json = """
        {
          "name": "DELETE",
          "scope": "PROFILE",
          "status": "%s",
          "description": "any_description"
        }
        """.formatted(status);

    mvc.perform(post("/permissions")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status must be one of " + EnumUtils.enumValuesAsString(PermissionStatus.class)));
  }

  @Test
  void shouldNotCreatePermissionWithoutDescription() throws Exception {
    final var json = """
        {
          "name": "DELETE",
          "scope": "PROFILE",
          "status": "ACTIVE"
        }
        """;

    mvc.perform(post("/permissions")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }

  @Test
  void shouldNotCreatePermissionWithNullDescription() throws Exception {
    final var json = """
        {
          "name": "DELETE",
          "scope": "PROFILE",
          "status": "ACTIVE",
          "description": null
        }
        """;

    mvc.perform(post("/permissions")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '"})
  void shouldNotCreatePermissionWithInvalidDescription(final String description) throws Exception {
    final var json = """
        {
          "name": "DELETE",
          "scope": "PROFILE",
          "status": "ACTIVE",
          "description": "%s"
        }
        """.formatted(description);

    mvc.perform(post("/permissions")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }

  @Test
  void shouldGetPermissionById() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    mvc.perform(get("/permissions/{id}", permission.getId())
            .header("Authorization", "Bearer " + authToken))
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
    mvc.perform(get("/permissions/{id}", id)
            .header("Authorization", "Bearer " + authToken))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Permission not found"));
  }

  @Test
  void shouldNotGetPermissionByIdWithInvalidId() throws Exception {
    final var id = "invalid_id";
    mvc.perform(get("/permissions/{id}", id)
            .header("Authorization", "Bearer " + authToken))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("Invalid UUID: " + id));
  }

  @Test
  void shouldUpdatePermission() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission("CREATE", "PROFILE", "any_description")));
    final var json = """
        {
          "name": "DELETE",
          "scope": "PROFILE",
          "status": "ACTIVE",
          "description": "updated_description"
        }
        """;


    mvc.perform(put("/permissions/{id}", permission.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isOk());

    final var savedPermission = repository.findById(permission.getId());

    assertThat(savedPermission).isPresent();
    assertThat(savedPermission.get().getName()).isEqualTo("DELETE");
    assertThat(savedPermission.get().getScope()).isEqualTo("PROFILE");
    assertThat(savedPermission.get().getStatus()).isEqualTo("ACTIVE");
    assertThat(savedPermission.get().getDescription()).isEqualTo("updated_description");
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
            .header("Authorization", "Bearer " + authToken)
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
            .header("Authorization", "Bearer " + authToken)
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
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }


  @ParameterizedTest
  @CsvSource(value = {"''", "'  '"})
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
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name must be one of " + EnumUtils.enumValuesAsString(PermissionName.class)));
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
            .header("Authorization", "Bearer " + authToken)
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
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("scope is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '"})
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
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("scope must be one of " + EnumUtils.enumValuesAsString(PermissionScope.class)));
  }

  @Test
  void shouldNotUpdatePermissionWithoutStatus() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission("CREATE", "PROFILE", "any_description")));
    final var json = """
        {
          "name": "DELETE",
          "scope": "PROFILE",
          "description": "any_description"
        }
        """;

    mvc.perform(put("/permissions/{id}", permission.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status is required"));
  }

  @Test
  void shouldNotUpdatePermissionWithNullStatus() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission("CREATE", "PROFILE", "any_description")));
    final var json = """
        {
          "name": "DELETE",
          "scope": "PROFILE",
          "status": null,
          "description": "any_description"
        }
        """;

    mvc.perform(put("/permissions/{id}", permission.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '"})
  void shouldNotUpdatePermissionWithInvalidStatus(final String status) throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission("CREATE", "PROFILE", "any_description")));
    final var json = """
        {
          "name": "DELETE",
          "scope": "PROFILE",
          "status": "%s",
          "description": "any_description"
        }
        """.formatted(status);

    mvc.perform(put("/permissions/{id}", permission.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status must be one of " + EnumUtils.enumValuesAsString(PermissionStatus.class)));
  }

  @Test
  void shouldNotUpdatePermissionWithoutDescription() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission("CREATE", "PROFILE", "any_description")));
    final var json = """
        {
          "name": "DELETE",
          "scope": "PROFILE",
          "status": "ACTIVE"
        }
        """;

    mvc.perform(put("/permissions/{id}", permission.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }

  @Test
  void shouldNotUpdatePermissionWithNullDescription() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission("CREATE", "PROFILE", "any_description")));
    final var json = """
        {
          "name": "DELETE",
          "scope": "PROFILE",
          "status": "ACTIVE",
          "description": null
        }
        """;

    mvc.perform(put("/permissions/{id}", permission.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '"})
  void shouldNotUpdatePermissionWithInvalidDescription(final String description) throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission("CREATE", "PROFILE", "any_description")));
    final var json = """
        {
          "name": "DELETE",
          "scope": "PROFILE",
          "status": "ACTIVE",
          "description": "%s"
        }
        """.formatted(description);

    mvc.perform(put("/permissions/{id}", permission.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("description is required"));
  }

  @Test
  void shouldDeletePermission() throws Exception {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    mvc.perform(delete("/permissions/{id}", permission.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json"))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldDoNothingWhenDeletePermissionWithNotFoundId() throws Exception {
    final var id = UUID.randomUUID().toString();
    mvc.perform(delete("/permissions/{id}", id)
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json"))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldNotDeletePermissionWhenIdIsNotAValidUUID() throws Exception {
    final var id = "invalid_id";
    mvc.perform(delete("/permissions/{id}", id)
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json"))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("Invalid UUID: " + id));
  }
}
