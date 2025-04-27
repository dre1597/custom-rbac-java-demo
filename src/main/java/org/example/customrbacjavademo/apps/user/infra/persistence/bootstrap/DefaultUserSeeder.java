package org.example.customrbacjavademo.apps.user.infra.persistence.bootstrap;

import org.example.customrbacjavademo.apps.user.domain.dto.NewPermissionDto;
import org.example.customrbacjavademo.apps.user.domain.dto.NewRoleDto;
import org.example.customrbacjavademo.apps.user.domain.dto.NewUserDto;
import org.example.customrbacjavademo.apps.user.domain.entities.Permission;
import org.example.customrbacjavademo.apps.user.domain.entities.Role;
import org.example.customrbacjavademo.apps.user.domain.entities.User;
import org.example.customrbacjavademo.apps.user.domain.enums.*;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class DefaultUserSeeder implements ApplicationListener<ContextRefreshedEvent> {
  @Value("${user.admin.name}")
  private String name;

  @Value("${user.admin.password}")
  private String password;

  private final PermissionJpaRepository permissionJpaRepository;
  private final RoleJpaRepository roleJpaRepository;
  private final UserJpaRepository userJpaRepository;

  public DefaultUserSeeder(
      final PermissionJpaRepository permissionJpaRepository,
      final RoleJpaRepository roleJpaRepository,
      final UserJpaRepository userJpaRepository
  ) {
    this.permissionJpaRepository = Objects.requireNonNull(permissionJpaRepository);
    this.roleJpaRepository = Objects.requireNonNull(roleJpaRepository);
    this.userJpaRepository = Objects.requireNonNull(userJpaRepository);
  }

  @Override
  public void onApplicationEvent(final ContextRefreshedEvent event) {
    this.seedPermissions();
    this.seedAdminRole();
    this.seedAdminUser();
  }

  private boolean isAlreadySeeded(final JpaRepository repository) {
    return repository.count() > 0;
  }

  private void seedPermissions() {
    if (isAlreadySeeded(permissionJpaRepository)) {
      return;
    }

    final var permissionPermissions = List.of(
        NewPermissionDto.of(
            PermissionName.READ.name(), PermissionScope.PERMISSION.name(), "Allows reading permissions", PermissionStatus.ACTIVE.name()
        ),
        NewPermissionDto.of(
            PermissionName.CREATE.name(), PermissionScope.PERMISSION.name(), "Allows creating permissions", PermissionStatus.ACTIVE.name()
        ),
        NewPermissionDto.of(
            PermissionName.UPDATE.name(), PermissionScope.PERMISSION.name(), "Allows updating permissions", PermissionStatus.ACTIVE.name()
        ),
        NewPermissionDto.of(
            PermissionName.DELETE.name(), PermissionScope.PERMISSION.name(), "Allows deleting permissions", PermissionStatus.ACTIVE.name()
        )
    );

    final var rolePermissions = List.of(
        NewPermissionDto.of(
            PermissionName.READ.name(), PermissionScope.ROLE.name(), "Allows reading roles", PermissionStatus.ACTIVE.name()
        ),
        NewPermissionDto.of(
            PermissionName.CREATE.name(), PermissionScope.ROLE.name(), "Allows creating roles", PermissionStatus.ACTIVE.name()
        ),
        NewPermissionDto.of(
            PermissionName.UPDATE.name(), PermissionScope.ROLE.name(), "Allows updating roles", PermissionStatus.ACTIVE.name()
        ),
        NewPermissionDto.of(
            PermissionName.DELETE.name(), PermissionScope.ROLE.name(), "Allows deleting roles", PermissionStatus.ACTIVE.name()
        )
    );

    final var userPermissions = List.of(
        NewPermissionDto.of(
            PermissionName.READ.name(), PermissionScope.USER.name(), "Allows reading users", PermissionStatus.ACTIVE.name()
        ),
        NewPermissionDto.of(
            PermissionName.CREATE.name(), PermissionScope.USER.name(), "Allows creating users", PermissionStatus.ACTIVE.name()
        ),
        NewPermissionDto.of(
            PermissionName.UPDATE.name(), PermissionScope.USER.name(), "Allows updating users", PermissionStatus.ACTIVE.name()
        ),
        NewPermissionDto.of(
            PermissionName.DELETE.name(), PermissionScope.USER.name(), "Allows deleting users", PermissionStatus.ACTIVE.name()
        )
    );

    final var profilePermissions = List.of(
        NewPermissionDto.of(
            PermissionName.READ.name(), PermissionScope.PROFILE.name(), "Allows reading profiles", PermissionStatus.ACTIVE.name()
        ),
        NewPermissionDto.of(
            PermissionName.UPDATE.name(), PermissionScope.PROFILE.name(), "Allows updating profiles", PermissionStatus.ACTIVE.name()
        )
    );

    final var permissions = Stream.of(
            permissionPermissions,
            rolePermissions,
            userPermissions,
            profilePermissions
        )
        .flatMap(List::stream)
        .toList();

    final var entities = permissions.stream()
        .map(dto -> {
          final var entity = Permission.newPermission(dto);
          return PermissionMapper.entityToJpa(entity);
        })
        .toList();

    permissionJpaRepository.saveAll(entities);
  }

  private void seedAdminRole() {
    if (isAlreadySeeded(roleJpaRepository)) {
      return;
    }

    final var permissionIds = permissionJpaRepository.findAll().stream().map(permission -> permission.getId().toString()).toList();

    final var dto = NewRoleDto.of("Admin", "Role with all permissions", RoleStatus.ACTIVE.name(), permissionIds);
    roleJpaRepository.save(RoleMapper.entityToJpa(Role.newRole(dto)));
  }

  private void seedAdminUser() {
    if (isAlreadySeeded(userJpaRepository)) {
      return;
    }

    final var roleId = roleJpaRepository.findAll().getFirst().getId().toString();

    final var dto = NewUserDto.of(this.name, this.password, UserStatus.ACTIVE.name(), roleId);
    userJpaRepository.save(UserMapper.entityToJpa(User.newUser(dto)));
  }
}
