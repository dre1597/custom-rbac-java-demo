package org.example.customrbacjavademo.apps.user.infra.api.controllers;

import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}

