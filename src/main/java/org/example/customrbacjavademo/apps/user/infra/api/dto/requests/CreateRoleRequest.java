package org.example.customrbacjavademo.apps.user.infra.api.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CreateRoleRequest(
    @JsonProperty("name") String name,
    @JsonProperty("description") String description,
    @JsonProperty("status") String status,
    @JsonProperty("permissions") List<String> permissionIds
) {
}
