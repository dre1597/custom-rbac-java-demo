package org.example.customrbacjavademo.apps.user.infra.api.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateUserRequest(
    @JsonProperty("name") String name,
    @JsonProperty("password") String password,
    @JsonProperty("status") String status,
    @JsonProperty("roleId") String roleId
) {
}
