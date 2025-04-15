package org.example.customrbacjavademo.common.domain.exceptions;

import java.util.List;

public class ValidationException extends RuntimeException {
  public ValidationException(final List<String> errors) {
    super(String.join(", ", errors));
  }

  public ValidationException(final String message) {
    super(message);
  }
}
