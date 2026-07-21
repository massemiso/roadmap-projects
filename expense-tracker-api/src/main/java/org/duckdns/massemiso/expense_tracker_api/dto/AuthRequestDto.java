package org.duckdns.massemiso.expense_tracker_api.dto;

import jakarta.validation.constraints.NotEmpty;

public record AuthRequestDto (
    @NotEmpty(message = "must be unique and not empty") String username,
    @NotEmpty(message = "must be not empty") String password
) {

  @Override
  public String toString() {
    return "AuthRequestDto{" +
        "username='" + username + '\'' +
        ", password='[MASKED]" + '\'' +
        '}';
  }
}
