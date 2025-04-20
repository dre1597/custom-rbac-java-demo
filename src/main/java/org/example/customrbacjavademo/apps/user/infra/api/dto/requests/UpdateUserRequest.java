package org.example.customrbacjavademo.apps.user.infra.api.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateUserRequest(
    @JsonProperty("name") String name,
    @JsonProperty("status") String status,
    @JsonProperty("roleId") String roleId
) {
}
