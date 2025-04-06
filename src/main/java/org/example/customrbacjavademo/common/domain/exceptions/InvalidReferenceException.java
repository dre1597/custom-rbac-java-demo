package org.example.customrbacjavademo.common.domain.exceptions;

public class InvalidReferenceException extends RuntimeException {
  public InvalidReferenceException(final String message) {
    super(message);
  }
}
