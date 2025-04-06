package org.example.customrbacjavademo.common.domain.exceptions;

public class AlreadyExistsException extends RuntimeException {
  public AlreadyExistsException(final String message) {
    super(message);
  }
}
