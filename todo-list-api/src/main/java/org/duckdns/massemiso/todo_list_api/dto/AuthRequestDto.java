package org.duckdns.massemiso.todo_list_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record AuthRequestDto(
    String name,
    @Email String email,
    @NotEmpty(message = "must not be empty") String password
) {
  @Override
  public String toString() {
    return "AuthRequestDto{" +
        "name='" + name + '\'' +
        ", email='" + email + '\'' +
        ", password=[MASKED]" + '\'' +
        '}';
  }
}
