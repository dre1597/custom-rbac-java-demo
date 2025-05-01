package org.example.customrbacjavademo.apps.auth.e2e;

import org.example.customrbacjavademo.E2ETest;
import org.example.customrbacjavademo.apps.auth.infra.persistence.RefreshTokenJpaRepository;
import org.example.customrbacjavademo.apps.auth.usecase.mappers.RefreshTokenMapper;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.RoleTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.UserTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
class AuthE2ETest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  private UserJpaRepository userRepository;

  @Autowired
  private RoleJpaRepository roleRepository;

  @Autowired
  private PermissionJpaRepository permissionRepository;

  @Autowired
  private RefreshTokenJpaRepository refreshTokenRepository;

  @Test
  void shouldLogin() throws Exception {
    final var permission = permissionRepository.save(
        PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission())
    );
    final var role = roleRepository.save(
        RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId())))
    );
    final var user = userRepository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var json = """
        {
          "name": "any_name",
          "password": "any_password"
        }
        """;

    mvc.perform(post("/auth/login")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.id").value(user.getId().toString()))
        .andExpect(jsonPath("$.user.name").value(user.getName()))
        .andExpect(jsonPath("$.user.roleId").value(role.getId().toString()))
        .andExpect(jsonPath("$.user.roleName").value(role.getName()))
        .andExpect(jsonPath("$.token").exists());
  }

  @Test
  void shouldThrowWhenAuthenticationFails() throws Exception {
    final var permission = permissionRepository.save(
        PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission())
    );
    final var role = roleRepository.save(
        RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId())))
    );
    userRepository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var json = """
        {
          "name": "any_name",
          "password": "invalid_password"
        }
        """;

    mvc.perform(post("/auth/login")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Invalid credentials"));
  }

  @Test
  void shouldThrowWhenUserNotFoundAfterAuthentication() throws Exception {
    final var json = """
        {
          "name": "any_name",
          "password": "any_password"
        }
        """;

    mvc.perform(post("/auth/login")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Invalid credentials"));
  }

  @Test
  void shouldThrowWhenUsernameIsNull() throws Exception {
    final var json = """
        {
          "name": null,
          "password": "any_password"
        }
        """;

    mvc.perform(post("/auth/login")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }

  @ParameterizedTest
  @EmptySource
  void shouldThrowWhenUsernameIsEmpty(final String username) throws Exception {
    final var json = """
        {
          "name": "%s",
          "password": "any_password"
        }
        """.formatted(username);

    mvc.perform(post("/auth/login")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required"));
  }

  @Test
  void shouldThrowWhenPasswordIsNull() throws Exception {
    final var json = """
        {
          "name": "any_name",
          "password": null
        }
        """;

    mvc.perform(post("/auth/login")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("password is required"));
  }

  @ParameterizedTest
  @EmptySource
  void shouldThrowWhenPasswordIsEmpty(final String password) throws Exception {
    final var json = """
        {
          "name": "any_name",
          "password": "%s"
        }
        """.formatted(password);

    mvc.perform(post("/auth/login")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("password is required"));
  }

  @Test
  void shouldThrowWhenUsernameAndPasswordAreNull() throws Exception {
    final var json = """
        {
          "name": null,
          "password": null
        }
        """;

    mvc.perform(post("/auth/login")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required, password is required"));
  }

  @ParameterizedTest
  @EmptySource
  void shouldThrowWhenUsernameAndPasswordAreEmpty(final String input) throws Exception {
    final var json = """
        {
          "name": "%s",
          "password": "%s"
        }
        """.formatted(input, input);

    mvc.perform(post("/auth/login")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("name is required, password is required"));
  }

  @Test
  void shouldRefreshToken() throws Exception {
    final var permission = permissionRepository.save(
        PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission())
    );
    final var role = roleRepository.save(
        RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId())))
    );
    final var user = userRepository.save(
        UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId()))
    );

    final var token = "valid_refresh_token";
    final var refreshToken = RefreshTokenMapper.entityToJpa(
        org.example.customrbacjavademo.apps.auth.domain.entities.RefreshToken.newRefreshToken(
            token, Instant.now().plusSeconds(3600), user.getId()
        )
    );
    refreshToken.setUser(user);
    refreshTokenRepository.save(refreshToken);

    final var json = """
        {
          "refreshToken": "%s"
        }
        """.formatted(token);

    mvc.perform(post("/auth/refresh")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists())
        .andExpect(jsonPath("$.refreshToken").exists())
        .andExpect(jsonPath("$.user.id").value(user.getId().toString()));
  }

  @Test
  void shouldThrowWhenRefreshTokenIsInvalid() throws Exception {
    final var json = """
        {
          "refreshToken": "invalid_token"
        }
        """;

    mvc.perform(post("/auth/refresh")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Invalid refresh token"));
  }

  @Test
  void shouldThrowWhenRefreshTokenIsNull() throws Exception {
    final var json = """
        {
          "refreshToken": null
        }
        """;

    mvc.perform(post("/auth/refresh")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Invalid refresh token"));
  }

  @ParameterizedTest
  @EmptySource
  void shouldThrowWhenRefreshTokenIsEmpty(final String refreshToken) throws Exception {
    final var json = """
        {
          "refreshToken": "%s"
        }
        """.formatted(refreshToken);

    mvc.perform(post("/auth/refresh")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Invalid refresh token"));
  }
}
