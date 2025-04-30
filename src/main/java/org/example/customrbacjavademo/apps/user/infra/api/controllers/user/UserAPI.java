package org.example.customrbacjavademo.apps.user.infra.api.controllers.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.CreateUserRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.UpdatePasswordRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.UpdateUserRequest;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.UserDetailsResponse;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.UserResponse;
import org.example.customrbacjavademo.common.domain.helpers.Pagination;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/users")
@Tag(name = "Users")
public interface UserAPI {
  @GetMapping
  @Operation(summary = "Get all users")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Users found successfully"),
      @ApiResponse(responseCode = "401", description = "Invalid token error"),
      @ApiResponse(responseCode = "403", description = "No access error"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<Pagination<UserResponse>> list(
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
  @Operation(summary = "Create a new user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User created successfully"),
      @ApiResponse(responseCode = "401", description = "Invalid token error"),
      @ApiResponse(responseCode = "403", description = "No access error"),
      @ApiResponse(responseCode = "409", description = "User already exists error"),
      @ApiResponse(responseCode = "422", description = "User validation error"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<Void> create(@RequestBody final CreateUserRequest input);

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get a user by id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User found successfully"),
      @ApiResponse(responseCode = "401", description = "Invalid token error"),
      @ApiResponse(responseCode = "403", description = "No access error"),
      @ApiResponse(responseCode = "404", description = "User not found error"),
      @ApiResponse(responseCode = "422", description = "UUID validation error"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<UserDetailsResponse> getById(@PathVariable String id);

  @PutMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @Operation(summary = "Update a user by id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User updated successfully"),
      @ApiResponse(responseCode = "401", description = "Invalid token error"),
      @ApiResponse(responseCode = "403", description = "No access error"),
      @ApiResponse(responseCode = "404", description = "User not found error"),
      @ApiResponse(responseCode = "422", description = "UUID validation error"),
      @ApiResponse(responseCode = "422", description = "User validation error"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<Void> update(@PathVariable String id, @RequestBody final UpdateUserRequest input);

  @PatchMapping(
      value = "/{id}/password",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @Operation(summary = "Update a user by id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User updated successfully"),
      @ApiResponse(responseCode = "401", description = "Invalid token error"),
      @ApiResponse(responseCode = "403", description = "No access error"),
      @ApiResponse(responseCode = "404", description = "User not found error"),
      @ApiResponse(responseCode = "422", description = "UUID validation error"),
      @ApiResponse(responseCode = "422", description = "User validation error"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<Void> updatePassword(@PathVariable String id, @RequestBody final UpdatePasswordRequest input);

  @DeleteMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @Operation(summary = "Delete a user by id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User deleted successfully"),
      @ApiResponse(responseCode = "401", description = "Invalid token error"),
      @ApiResponse(responseCode = "403", description = "No access error"),
      @ApiResponse(responseCode = "422", description = "UUID validation error"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<Void> delete(@PathVariable String id);
}
