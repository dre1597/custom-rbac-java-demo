package org.example.customrbacjavademo.domain.dto;

public record UpdateUserDto(String name) {
  public static UpdateUserDto of(final String name) {
    return new UpdateUserDto(name);
  }
}
