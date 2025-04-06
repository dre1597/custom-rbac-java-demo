package org.example.customrbacjavademo.common.domain.exceptions;

public class NotFoundException extends RuntimeException {
  public NotFoundException(final String message) {
    super(message);
  }
}
