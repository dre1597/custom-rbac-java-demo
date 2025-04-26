package org.example.customrbacjavademo.apps.auth.infra.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.customrbacjavademo.apps.auth.infra.api.dto.requests.LoginRequest;
import org.example.customrbacjavademo.apps.auth.infra.api.dto.responses.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/auth")
@Tag(name = "Auth")
public interface AuthAPI {
  @PostMapping("/login")
  @Operation(summary = "Login")
  @SecurityRequirements()
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Login successful"),
          @ApiResponse(responseCode = "401", description = "Unauthorized"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
      }
  )
  ResponseEntity<LoginResponse> login(@RequestBody final LoginRequest input);
}
