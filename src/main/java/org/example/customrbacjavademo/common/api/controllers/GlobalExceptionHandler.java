package org.example.customrbacjavademo.common.api.controllers;

import org.example.customrbacjavademo.common.domain.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(value = NotFoundException.class)
  public ResponseEntity<ApiError> handleNotFoundException(final NotFoundException exception) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(ApiError.from(exception.getMessage(), HttpStatus.NOT_FOUND.value()));
  }

  @ExceptionHandler(value = InvalidReferenceException.class)
  public ResponseEntity<ApiError> handleInvalidReferenceException(final InvalidReferenceException exception) {
    return ResponseEntity
        .status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(ApiError.from(exception.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value()));
  }

  @ExceptionHandler(value = AlreadyExistsException.class)
  public ResponseEntity<ApiError> handleAlreadyExistsException(final AlreadyExistsException exception) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(ApiError.from(exception.getMessage(), HttpStatus.CONFLICT.value()));
  }

  @ExceptionHandler(value = ValidationException.class)
  public ResponseEntity<ApiError> handleValidationException(final ValidationException exception) {
    return ResponseEntity
        .status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(ApiError.from(exception.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value()));
  }

  @ExceptionHandler(value = UnauthorizedException.class)
  public ResponseEntity<ApiError> handleUnauthorizedException(final UnauthorizedException exception) {
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(ApiError.from(exception.getMessage(), HttpStatus.UNAUTHORIZED.value()));
  }

  @ExceptionHandler(value = AuthenticationException.class)
  public ResponseEntity<ApiError> handleAuthenticationException() {
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(ApiError.from("Invalid credentials", HttpStatus.UNAUTHORIZED.value()));
  }

  @ExceptionHandler(value = UsernameNotFoundException.class)
  public ResponseEntity<ApiError> handleUsernameNotFoundException() {
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(ApiError.from("Invalid credentials", HttpStatus.UNAUTHORIZED.value()));
  }

  @ExceptionHandler(value = NoAccessException.class)
  public ResponseEntity<ApiError> handleNoAccessException(final NoAccessException exception) {
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(ApiError.from(exception.getMessage(), HttpStatus.FORBIDDEN.value()));
  }
}

