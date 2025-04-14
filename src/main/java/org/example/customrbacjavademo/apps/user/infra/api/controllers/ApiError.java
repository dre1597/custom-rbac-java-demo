package org.example.customrbacjavademo.apps.user.infra.api.controllers;

public record ApiError(String message, int status) {
  static ApiError from(final String message, final int status) {
    return new ApiError(message, status);
  }
}
