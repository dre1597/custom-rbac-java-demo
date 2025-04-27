package org.example.customrbacjavademo.apps.auth.infra.api.dto.responses;

public record UserLoginResponse(
    String id,
    String name,
    String roleId,
    String roleName
) {
}
