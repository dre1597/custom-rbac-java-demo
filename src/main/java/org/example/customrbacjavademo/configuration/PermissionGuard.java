package org.example.customrbacjavademo.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.example.customrbacjavademo.apps.auth.domain.services.JwtService;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.common.domain.exceptions.NoAccessException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class PermissionGuard {
  private final JwtService jwtService;
  private final RoleJpaRepository roleJpaRepository;

  public PermissionGuard(final JwtService jwtService, final RoleJpaRepository roleJpaRepository) {
    this.jwtService = Objects.requireNonNull(jwtService);
    this.roleJpaRepository = Objects.requireNonNull(roleJpaRepository);
  }

  public void checkPermission(final HttpServletRequest request, RequiredPermission requiredPermission) throws NoAccessException {
    final var token = extractToken(request);
    final var roleId = jwtService.extractRoleId(token);

    final var hasPermission = this.hasPermission(UUID.fromString(roleId), requiredPermission);

    if (!hasPermission) {
      throw new NoAccessException("You don't have permission to perform this action");
    }
  }

  private String extractToken(HttpServletRequest request) throws NoAccessException {
    final var bearerToken = request.getHeader("Authorization");
    if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
      throw new NoAccessException("Invalid token");
    }
    return bearerToken.substring(7);
  }

  private boolean hasPermission(final UUID roleId, final RequiredPermission requiredPermission) {
    final var role = roleJpaRepository.findWithPermissionsById(roleId).orElse(null);

    if (role == null) {
      return false;
    }

    return role.getPermissions().stream()
        .anyMatch(permission ->
            permission.getName().equals(requiredPermission.name().toString()) &&
                permission.getScope().equals(requiredPermission.scope().toString())
        );
  }
}


