package org.example.customrbacjavademo.apps.user.infra.api.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record PermissionResponse(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("scope") String scope,
    @JsonProperty("description") String description,
    @JsonProperty("status") String status,
    @JsonProperty("createdAt") Instant createdAt,
    @JsonProperty("updatedAt") Instant updatedAt
) {
}
