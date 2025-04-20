package org.example.customrbacjavademo.apps.user.infra.api.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdatePasswordRequest(
    @JsonProperty("oldPassword") String oldPassword,
    @JsonProperty("newPassword") String newPassword
) {
}
