package org.example.customrbacjavademo.domain.dto;

public record UpdateUserDto(
    String name,
    String password
) {
  public static UpdateUserDto of(final String name, final String password) {
    return new UpdateUserDto(name, password);
  }
}
