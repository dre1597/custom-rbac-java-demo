package org.example.customrbacjavademo.apps.user.infra.api.controllers;

import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.example.customrbacjavademo.common.domain.exceptions.InvalidReferenceException;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
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
}

