package org.example.customrbacjavademo.common.domain.exceptions;

import java.util.List;

public class ValidationException extends RuntimeException {
  private final List<String> errors;

  public ValidationException(final List<String> errors) {
    super(String.join(", ", errors));
    this.errors = errors;
  }

  public List<String> getErrors() {
    return errors;
  }
}
