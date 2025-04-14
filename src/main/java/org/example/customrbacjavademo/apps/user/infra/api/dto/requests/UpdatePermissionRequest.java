package org.example.customrbacjavademo.apps.user.infra.api.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdatePermissionRequest(
    @JsonProperty("name") String name,
    @JsonProperty("scope") String scope,
    @JsonProperty("status") String status,
    @JsonProperty("description") String description
) {
}
