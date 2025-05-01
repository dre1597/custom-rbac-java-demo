package org.example.customrbacjavademo.apps.auth.usecase;

import org.example.customrbacjavademo.IntegrationTest;
import org.example.customrbacjavademo.apps.auth.domain.dto.RefreshTokenDto;
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
import org.example.customrbacjavademo.common.domain.exceptions.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
class RefreshTokenUseCaseIntegrationTest {
  @Autowired
  private RefreshTokenUseCase useCase;

  @Autowired
  private RefreshTokenJpaRepository refreshTokenRepository;

  @Autowired
  private UserJpaRepository userRepository;

  @Autowired
  private RoleJpaRepository roleRepository;

  @Autowired
  private PermissionJpaRepository permissionRepository;

  @Test
  void shouldRefreshToken() {
    final var permission = permissionRepository.save(
        PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission())
    );
    final var role = roleRepository.save(
        RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId())))
    );
    final var user = userRepository.save(
        UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId()))
    );

    final var token = "simple_token";
    final var refreshToken = RefreshTokenMapper.entityToJpa(
        org.example.customrbacjavademo.apps.auth.domain.entities.RefreshToken.newRefreshToken(
            token, Instant.now().plusSeconds(3600), user.getId()
        )
    );
    refreshToken.setUser(user);
    refreshTokenRepository.save(refreshToken);

    final var result = useCase.execute(new RefreshTokenDto(token));

    assertNotNull(result.token());
    assertNotNull(result.refreshToken());
    assertEquals(user.getId().toString(), result.user().id());
  }

  @Test
  void shouldThrowWhenTokenInvalid() {
    final var ex = assertThrows(UnauthorizedException.class,
        () -> useCase.execute(new RefreshTokenDto("invalid_token")));

    assertEquals("Invalid refresh token", ex.getMessage());
  }
}
