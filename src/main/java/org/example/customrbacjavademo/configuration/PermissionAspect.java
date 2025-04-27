package org.example.customrbacjavademo.configuration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Aspect
@Component
public class PermissionAspect {
  private final PermissionGuard permissionGuard;

  public PermissionAspect(final PermissionGuard permissionGuard) {
    this.permissionGuard = Objects.requireNonNull(permissionGuard);
  }

  @Around("@annotation(requiredPermission)")
  public Object checkPermission(final ProceedingJoinPoint joinPoint, final RequiredPermission requiredPermission) throws Throwable {
    final var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

    permissionGuard.checkPermission(request, requiredPermission);

    return joinPoint.proceed();
  }
}
