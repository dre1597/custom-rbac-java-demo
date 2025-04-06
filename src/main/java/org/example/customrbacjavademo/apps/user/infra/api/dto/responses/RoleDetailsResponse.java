package org.example.customrbacjavademo.apps.user.infra.api.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RoleDetailsResponse(
    @JsonProperty("id") UUID id,
    @JsonProperty("name") String name,
    @JsonProperty("description") String description,
    @JsonProperty("status") String status,
    @JsonProperty("createdAt") Instant createdAt,
    @JsonProperty("updatedAt") Instant updatedAt,
    @JsonProperty("permissions") List<PermissionResponse> permissions
) {
}

