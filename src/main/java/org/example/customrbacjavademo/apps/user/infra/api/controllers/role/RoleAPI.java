package org.example.customrbacjavademo.apps.user.infra.api.controllers.role;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.CreateRoleRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.UpdateRoleRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.RoleDetailsResponse;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.RoleResponse;
import org.example.customrbacjavademo.common.domain.helpers.Pagination;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/roles")
@Tag(name = "Roles")
public interface RoleAPI {
  @GetMapping
  @Operation(summary = "Get all roles")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Roles found successfully"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<Pagination<RoleResponse>> list(
      @RequestParam(name = "search", required = false, defaultValue = "") final String search,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
      @RequestParam(name = "sort", required = false, defaultValue = "name") final String sort,
      @RequestParam(name = "dir", required = false, defaultValue = "asc") final String direction
  );

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @Operation(summary = "Create a new role")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Role created successfully"),
      @ApiResponse(responseCode = "409", description = "Role already exists error"),
      @ApiResponse(responseCode = "422", description = "Role validation error"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<Void> create(@RequestBody final CreateRoleRequest input);

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get a role by id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Role found successfully"),
      @ApiResponse(responseCode = "404", description = "Role not found error"),
      @ApiResponse(responseCode = "422", description = "UUID validation error"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<RoleDetailsResponse> getById(@PathVariable String id);

  @PutMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @Operation(summary = "Update a role by id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Role updated successfully"),
      @ApiResponse(responseCode = "404", description = "Role not found error"),
      @ApiResponse(responseCode = "422", description = "UUID validation error"),
      @ApiResponse(responseCode = "422", description = "Role validation error"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<Void> update(@PathVariable String id, @RequestBody final UpdateRoleRequest input);

  @DeleteMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @Operation(summary = "Delete a role by id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Role deleted successfully"),
      @ApiResponse(responseCode = "422", description = "UUID validation error"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<Void> delete(@PathVariable String id);
}
