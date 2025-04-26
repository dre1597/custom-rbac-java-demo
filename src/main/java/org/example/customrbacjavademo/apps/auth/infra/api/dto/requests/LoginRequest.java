package org.example.customrbacjavademo.apps.auth.infra.api.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginRequest(
    @JsonProperty("name") String name,
    @JsonProperty("password") String password
) {
}
