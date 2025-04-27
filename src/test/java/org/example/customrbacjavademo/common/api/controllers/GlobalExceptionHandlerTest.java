package org.example.customrbacjavademo.common.api.controllers;

import org.example.customrbacjavademo.common.domain.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {
  private GlobalExceptionHandler globalExceptionHandler;

  @BeforeEach
  void setUp() {
    globalExceptionHandler = new GlobalExceptionHandler();
  }

  @Test
  void shouldHandleNotFoundException() {
    final var errorMessage = "Resource not found";
    final var exception = new NotFoundException(errorMessage);

    final var response = globalExceptionHandler.handleNotFoundException(exception);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals(new ApiError(errorMessage, HttpStatus.NOT_FOUND.value()), response.getBody());
  }

  @Test
  void shouldHandleInvalidReferenceException() {
    final var errorMessage = "Invalid reference";
    final var exception = new InvalidReferenceException(errorMessage);

    final var response = globalExceptionHandler.handleInvalidReferenceException(exception);

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    assertEquals(new ApiError(errorMessage, HttpStatus.UNPROCESSABLE_ENTITY.value()), response.getBody());
  }

  @Test
  void shouldHandleAlreadyExistsException() {
    final var errorMessage = "Resource already exists";
    final var exception = new AlreadyExistsException(errorMessage);

    final var response = globalExceptionHandler.handleAlreadyExistsException(exception);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals(new ApiError(errorMessage, HttpStatus.CONFLICT.value()), response.getBody());
  }

  @Test
  void shouldHandleValidationException() {
    final var errorMessage = "Validation failed";
    final var exception = new ValidationException(errorMessage);

    final var response = globalExceptionHandler.handleValidationException(exception);

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    assertEquals(new ApiError(errorMessage, HttpStatus.UNPROCESSABLE_ENTITY.value()), response.getBody());
  }

  @Test
  void shouldHandleUnauthorizedException() {
    final var errorMessage = "Unauthorized";
    final var exception = new UnauthorizedException(errorMessage);

    final var response = globalExceptionHandler.handleUnauthorizedException(exception);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals(new ApiError(errorMessage, HttpStatus.UNAUTHORIZED.value()), response.getBody());
  }

  @Test
  void shouldHandleAuthenticationException() {
    final var response = globalExceptionHandler.handleAuthenticationException();

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals(new ApiError("Invalid credentials", HttpStatus.UNAUTHORIZED.value()), response.getBody());
  }

  @Test
  void shouldHandleUsernameNotFoundException() {
    final var response = globalExceptionHandler.handleUsernameNotFoundException();

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals(new ApiError("Invalid credentials", HttpStatus.UNAUTHORIZED.value()), response.getBody());
  }

  @Test
  void shouldHandleNoAccessException() {
    final var errorMessage = "Access denied";
    final var exception = new NoAccessException(errorMessage);

    final var response = globalExceptionHandler.handleNoAccessException(exception);

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertEquals(new ApiError(errorMessage, HttpStatus.FORBIDDEN.value()), response.getBody());
  }
}

