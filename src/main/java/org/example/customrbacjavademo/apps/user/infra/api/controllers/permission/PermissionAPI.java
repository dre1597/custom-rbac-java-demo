package org.example.customrbacjavademo.apps.user.infra.api.controllers.permission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.CreatePermissionRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.UpdatePermissionRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.PermissionResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
  ResponseEntity<Void> create(@RequestBody final CreatePermissionRequest input);

  @GetMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @Operation(summary = "Get a permission by id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Permission found successfully"),
      @ApiResponse(responseCode = "404", description = "Permission not found error"),
      @ApiResponse(responseCode = "422", description = "UUID validation error"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<PermissionResponse> getById(@PathVariable String id);

  @PutMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @Operation(summary = "Update a permission by id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Permission updated successfully"),
      @ApiResponse(responseCode = "404", description = "Permission not found error"),
      @ApiResponse(responseCode = "422", description = "Permission validation error"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<Void> update(@PathVariable String id, @RequestBody final UpdatePermissionRequest input);

  @DeleteMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @Operation(summary = "Delete a permission by id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Permission deleted successfully"),
      @ApiResponse(responseCode = "422", description = "UUID validation error"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<Void> delete(@PathVariable String id);
}
