package org.example.customrbacjavademo.apps.user.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.customrbacjavademo.E2ETest;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.RoleTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.UserTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.bootstrap.DefaultUserSeeder;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
class UserE2ETest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  private UserJpaRepository repository;

  @Autowired
  private RoleJpaRepository roleRepository;

  @Autowired
  private PermissionJpaRepository permissionRepository;

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
  void shouldListUsesWithoutSearchTerm() throws Exception {
    mvc.perform(get("/users")
            .header("Authorization", "Bearer " + authToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currentPage").value(0))
        .andExpect(jsonPath("$.perPage").value(10))
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items[0].id").exists())
        .andExpect(jsonPath("$.items[0].name").exists())
        .andExpect(jsonPath("$.items[0].status").exists())
        .andExpect(jsonPath("$.items[0].createdAt").exists())
        .andExpect(jsonPath("$.items[0].updatedAt").exists());
  }

  @Test
  void shouldListUsesWithSearchTerm() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));
    final var secondUser = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser("other_name", role.getId())));

    mvc.perform(get("/users?search=other")
            .header("Authorization", "Bearer " + authToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currentPage").value(0))
        .andExpect(jsonPath("$.perPage").value(10))
        .andExpect(jsonPath("$.total").value(1))
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items", hasSize(1)))
        .andExpect(jsonPath("$.items[*]", containsInAnyOrder(
            allOf(
                hasEntry("id", secondUser.getId().toString()),
                hasEntry("name", secondUser.getName()),
                hasEntry("status", secondUser.getStatus())
            )
        )))
        .andExpect(jsonPath("$.items[0].createdAt").exists())
        .andExpect(jsonPath("$.items[0].updatedAt").exists());
  }

  @Test
  void shouldCreateUser() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));

    final var json = """
        {
          "name": "any_name",
          "password": "any_password",
          "status": "ACTIVE",
          "roleId": "%s"
        }
        """.formatted(role.getId());

    final var result = mvc.perform(post("/users")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isCreated());

    final var response = result.andReturn().getResponse();
    assertThat(response.getHeader("Location")).isNotNull();

    final var users = repository.existsByName("any_name");
    assertThat(users).isTrue();
  }

  @Test
  void shouldNotCreateUserWithTheSameName() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var json = """
        {
          "name": "%s",
          "password": "any_password",
          "status": "ACTIVE",
          "roleId": "%s"
        }
        """.formatted(user.getName(), role.getId());

    mvc.perform(post("/users")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("User already exists"));
  }

  @Test
  void shouldNotCreateUserWithoutName() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));

    final var json = """
        {
          "password": "any_password",
          "status": "ACTIVE",
          "roleId": "%s"
        }
        """.formatted(role.getId());

    mvc.perform(post("/users")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }

  @Test
  void shouldNotCreateUserWithNullName() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));

    final var json = """
        {
          "name": null,
          "password": "any_password",
          "status": "ACTIVE",
          "roleId": "%s"
        }
        """.formatted(role.getId());

    mvc.perform(post("/users")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '"})
  void shouldNotCreateUserWithInvalidName(final String name) throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));

    final var json = """
        {
          "name": "%s",
          "password": "any_password",
          "status": "ACTIVE",
          "roleId": "%s"
        }
        """.formatted(name, role.getId());

    mvc.perform(post("/users")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }

  @Test
  void shouldNotCreateUserWithoutPassword() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));

    final var json = """
        {
          "name": "any_name",
          "status": "ACTIVE",
          "roleId": "%s"
        }
        """.formatted(role.getId());

    mvc.perform(post("/users")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("password is required"));
  }

  @Test
  void shouldNotCreateUserWithNullPassword() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));

    final var json = """
        {
          "name": "any_name",
          "password": null,
          "status": "ACTIVE",
          "roleId": "%s"
        }
        """.formatted(role.getId());

    mvc.perform(post("/users")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("password is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '"})
  void shouldNotCreateUserWithInvalidPassword(final String password) throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));

    final var json = """
        {
          "name": "any_name",
          "password": "%s",
          "status": "ACTIVE",
          "roleId": "%s"
        }
        """.formatted(password, role.getId());

    mvc.perform(post("/users")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("password is required"));
  }

  @Test
  void shouldNotCreateUserWithoutStatus() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));

    final var json = """
        {
          "name": "any_name",
          "password": "any_password",
          "roleId": "%s"
        }
        """.formatted(role.getId());

    mvc.perform(post("/users")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status is required"));
  }

  @Test
  void shouldNotCreateUserWithNullStatus() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));

    final var json = """
        {
          "name": "any_name",
          "password": "any_password",
          "status": null,
          "roleId": "%s"
        }
        """.formatted(role.getId());

    mvc.perform(post("/users")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '"})
  void shouldNotCreateUserWithInvalidStatus(final String status) throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));

    final var json = """
        {
          "name": "any_name",
          "password": "any_password",
          "status": "%s",
          "roleId": "%s"
        }
        """.formatted(status, role.getId());

    mvc.perform(post("/users")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status must be one of ACTIVE, INACTIVE"));
  }

  @Test
  void shouldNotCreateUserWithoutRole() throws Exception {
    final var json = """
        {
          "name": "any_name",
          "password": "any_password",
          "status": "ACTIVE"
        }
        """;

    mvc.perform(post("/users")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("roleId is required"));
  }

  @Test
  void shouldNotCreateUserWithNullRole() throws Exception {
    final var json = """
        {
          "name": "any_name",
          "password": "any_password",
          "status": "ACTIVE",
          "roleId": null
        }
        """;

    mvc.perform(post("/users")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("roleId is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '"})
  void shouldNotCreateUserWithInvalidRole(final String roleId) throws Exception {
    final var json = """
        {
          "name": "any_name",
          "password": "any_password",
          "status": "ACTIVE",
          "roleId": "%s"
        }
        """.formatted(roleId);

    mvc.perform(post("/users")
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("roleId is required"));
  }

  @Test
  void shouldGetUserById() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    mvc.perform(get("/users/{id}", user.getId())
            .header("Authorization", "Bearer " + authToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(user.getName()))
        .andExpect(jsonPath("$.status").value(user.getStatus()))
        .andExpect(jsonPath("$.role.id").value(role.getId().toString()))
        .andExpect(jsonPath("$.role.name").value(role.getName()))
        .andExpect(jsonPath("$.role.description").value(role.getDescription()))
        .andExpect(jsonPath("$.role.status").value(role.getStatus()))
        .andExpect(jsonPath("$.role.createdAt").exists())
        .andExpect(jsonPath("$.role.updatedAt").exists());
  }

  @Test
  void shouldNotGetUserByIdWithNotFoundId() throws Exception {
    final var id = UUID.randomUUID().toString();
    mvc.perform(get("/users/{id}", id)
            .header("Authorization", "Bearer " + authToken))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User not found"));
  }

  @Test
  void shouldNotGetUserByIdWithInvalidId() throws Exception {
    final var id = "invalid_id";
    mvc.perform(get("/users/{id}", id)
            .header("Authorization", "Bearer " + authToken))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("Invalid UUID: " + id));
  }

  @Test
  void shouldUpdateUser() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var newRole = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole("new_role", List.of(permission.getId()))));

    final var json = """
        {
          "name": "updated_name",
          "status": "INACTIVE",
          "roleId": "%s"
        }
        """.formatted(newRole.getId());

    mvc.perform(put("/users/{id}", user.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isOk());
  }

  @Test
  void shouldNotUpdateUserToADuplicateName() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));
    final var userToUpdate = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser("other_name", role.getId())));

    final var json = """
        {
          "name": "%s",
          "status": "%s",
          "roleId": "%s"
        }
        """.formatted(user.getName(), userToUpdate.getStatus(), role.getId());

    mvc.perform(put("/users/{id}", userToUpdate.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("User already exists"));
  }

  @Test
  void shouldNotUpdateUserWithoutName() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var json = """
        {
          "status": "%s",
          "roleId": "%s"
        }
        """.formatted(user.getStatus(), role.getId());

    mvc.perform(put("/users/{id}", user.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }

  @Test
  void shouldNotUpdateUserWithNullName() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var json = """
        {
          "name": null,
          "status": "%s",
          "roleId": "%s"
        }
        """.formatted(user.getStatus(), role.getId());

    mvc.perform(put("/users/{id}", user.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '"})
  void shouldNotUpdateUserWithInvalidName(final String name) throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var json = """
        {
          "name": "%s",
          "status": "%s",
          "roleId": "%s"
        }
        """.formatted(name, user.getStatus(), role.getId());

    mvc.perform(put("/users/{id}", user.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }

  @Test
  void shouldNotUpdateUserWithoutStatus() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var json = """
        {
          "name": "%s",
          "roleId": "%s"
        }
        """.formatted(user.getName(), role.getId());

    mvc.perform(put("/users/{id}", user.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status is required"));
  }

  @Test
  void shouldNotUpdateUserWithNullStatus() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var json = """
        {
          "name": "%s",
          "status": null,
          "roleId": "%s"
        }
        """.formatted(user.getName(), role.getId());

    mvc.perform(put("/users/{id}", user.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '"})
  void shouldNotUpdateUserWithInvalidStatus(final String status) throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var json = """
        {
          "name": "%s",
          "status": "%s",
          "roleId": "%s"
        }
        """.formatted(user.getName(), status, role.getId());

    mvc.perform(put("/users/{id}", user.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("status must be one of ACTIVE, INACTIVE"));
  }

  @Test
  void shouldNotUpdateUserWithoutRole() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var json = """
        {
          "name": "%s",
          "status": "%s"
        }
        """.formatted(user.getName(), user.getStatus());

    mvc.perform(put("/users/{id}", user.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("roleId is required"));
  }

  @Test
  void shouldNotUpdateUserWithNullRole() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var json = """
        {
          "name": "%s",
          "status": "%s",
          "roleId": null
        }
        """.formatted(user.getName(), user.getStatus());

    mvc.perform(put("/users/{id}", user.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("roleId is required"));
  }

  @ParameterizedTest
  @CsvSource(value = {"''", "'  '"})
  void shouldNotUpdateUserWithInvalidRole(final String roleId) throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var json = """
        {
          "name": "%s",
          "status": "%s",
          "roleId": "%s"
        }
        """.formatted(user.getName(), user.getStatus(), roleId);

    mvc.perform(put("/users/{id}", user.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("roleId is required"));
  }

  @Test
  void shouldUpdatePassword() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser("any_name", "any_password", role.getId())));

    final var json = """
        {
          "oldPassword": "any_password",
          "newPassword": "new_password"
        }
        """;

    mvc.perform(patch("/users/{id}/password", user.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isOk());
  }

  @Test
  void shouldNotUpdatePasswordWhenOldPasswordIsIncorrect() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser("any_name", "any_password", role.getId())));

    final var json = """
        {
          "oldPassword": "invalid_password",
          "newPassword": "new_password"
        }
        """;

    mvc.perform(patch("/users/{id}/password", user.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("User not found or old password is invalid"));
  }

  @Test
  void shouldNotUpdatePasswordWhenMissingOldPassword() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser("any_name", "any_password", role.getId())));

    final var json = """
        {
          "newPassword": "new_password"
        }
        """;

    mvc.perform(patch("/users/{id}/password", user.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("User not found or old password is invalid"));
  }

  @Test
  void shouldNotUpdatePasswordWhenMissingNewPassword() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser("any_name", "any_password", role.getId())));

    final var json = """
        {
          "oldPassword": "any_password"
        }
        """;

    mvc.perform(patch("/users/{id}/password", user.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("new password is required"));
  }

  @Test
  void shouldDeleteUser() throws Exception {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    mvc.perform(delete("/users/{id}", user.getId())
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json"))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldDoNothingWhenDeleteUserWithNotFoundId() throws Exception {
    final var id = UUID.randomUUID().toString();
    mvc.perform(delete("/users/{id}", id)
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json"))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldNotDeleteUserWithInvalidId() throws Exception {
    final var id = "invalid_id";
    mvc.perform(delete("/users/{id}", id)
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json"))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("Invalid UUID: " + id));
  }
}
