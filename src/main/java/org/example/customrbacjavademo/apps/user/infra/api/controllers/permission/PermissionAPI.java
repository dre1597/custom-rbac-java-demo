package org.example.customrbacjavademo.apps.user.infra.api.controllers.permission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.CreatePermissionRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "/permissions")
@Tag(name = "Permissions")
public interface PermissionAPI {
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @Operation(summary = "Create a new permission")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Permission created successfully"),
      @ApiResponse(responseCode = "409", description = "Permission already exists error"),
      @ApiResponse(responseCode = "422", description = "Permission validation error"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<Void> createPermission(@RequestBody final CreatePermissionRequest input);
}
