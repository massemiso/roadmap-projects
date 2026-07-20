package org.duckdns.massemiso.expense_tracker_api.dto;

import jakarta.validation.constraints.NotEmpty;

public record AuthRequestDto (
    @NotEmpty String username,
    @NotEmpty String password
) {

  @Override
  public String toString() {
    return "AuthRequestDto{" +
        "username='" + username + '\'' +
        ", password='[MASKED]" + '\'' +
        '}';
  }
}
