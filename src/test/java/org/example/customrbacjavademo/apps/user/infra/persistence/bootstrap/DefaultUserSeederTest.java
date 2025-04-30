package org.example.customrbacjavademo.apps.user.infra.persistence.bootstrap;

import org.example.customrbacjavademo.apps.auth.infra.persistence.RefreshTokenJpaRepository;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "user.admin.name=admin",
    "user.admin.password=password"
})
class DefaultUserSeederTest {
  @Autowired
  private PermissionJpaRepository permissionJpaRepository;

  @Autowired
  private RoleJpaRepository roleJpaRepository;

  @Autowired
  private UserJpaRepository userJpaRepository;

  @Autowired
  private RefreshTokenJpaRepository refreshTokenJpaRepository;

  @Autowired
  private DefaultUserSeeder userSeeder;

  @Test
  void shouldInjectProperties() {
    assertThat(userSeeder)
        .hasFieldOrPropertyWithValue("name", "admin")
        .hasFieldOrPropertyWithValue("password", "password");
  }

  @Test
  void shouldSeed() {
    assertThat(permissionJpaRepository.count()).isPositive();

    assertThat(permissionJpaRepository.findByNameAndScope(PermissionName.READ.name(), PermissionScope.PERMISSION.name()))
        .isPresent();
    assertThat(permissionJpaRepository.findByNameAndScope(PermissionName.READ.name(), PermissionScope.ROLE.name()))
        .isPresent();
    assertThat(permissionJpaRepository.findByNameAndScope(PermissionName.READ.name(), PermissionScope.USER.name()))
        .isPresent();
    assertThat(permissionJpaRepository.findByNameAndScope(PermissionName.READ.name(), PermissionScope.PROFILE.name()))
        .isPresent();

    assertThat(roleJpaRepository.count()).isEqualTo(1);
    final var adminRole = roleJpaRepository.findWithPermissionsByName("Admin");
    assertThat(adminRole).isPresent();

    assertThat(adminRole.get().getPermissions()).hasSize((int) permissionJpaRepository.count());

    assertThat(userJpaRepository.count()).isEqualTo(1);
    final var adminUser = userJpaRepository.findByName("admin");
    assertThat(adminUser).isPresent();

    assertThat(adminUser.get().getRole().getName()).isEqualTo("Admin");
  }

  @Test
  void shouldSeedManually() {
    refreshTokenJpaRepository.deleteAll();
    userJpaRepository.deleteAll();
    roleJpaRepository.deleteAll();
    permissionJpaRepository.deleteAll();

    userSeeder.seedAllManually();

    assertThat(permissionJpaRepository.count()).isPositive();
    assertThat(roleJpaRepository.count()).isEqualTo(1);
    assertThat(userJpaRepository.count()).isEqualTo(1);
  }

  @Test
  void shouldNotSeedIfAlreadySeeded() {
    final var permissionCount = permissionJpaRepository.count();
    final var roleCount = roleJpaRepository.count();
    final var userCount = userJpaRepository.count();

    userSeeder.seedAllManually();

    assertThat(permissionJpaRepository.count()).isEqualTo(permissionCount);
    assertThat(roleJpaRepository.count()).isEqualTo(roleCount);
    assertThat(userJpaRepository.count()).isEqualTo(userCount);
  }
}
