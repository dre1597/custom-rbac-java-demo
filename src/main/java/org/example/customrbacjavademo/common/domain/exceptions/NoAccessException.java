package org.example.customrbacjavademo.common.domain.exceptions;

public class NoAccessException extends RuntimeException {
  public NoAccessException(String message) {
    super(message);
  }
}
