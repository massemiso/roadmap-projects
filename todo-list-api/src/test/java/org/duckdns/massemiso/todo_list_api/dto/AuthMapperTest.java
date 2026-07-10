package org.duckdns.massemiso.todo_list_api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.duckdns.massemiso.todo_list_api.entity.User;
import org.junit.jupiter.api.Test;

public class AuthMapperTest {

  private final AuthMapper authMapper = new AuthMapper();

  @Test
  void toUser_ShouldMapCorrectly() {
    AuthRequestDto request = new AuthRequestDto("Name", "email@ex.com", "pass");
    User user = authMapper.toUser(request, "encoded");

    assertEquals("Name", user.getName());
    assertEquals("email@ex.com", user.getEmail());
    assertEquals("encoded", user.getPassword());
  }

  @Test
  void toResponse_ShouldMapToken() {
    AuthResponseDto response = authMapper.toResponse("token");
    assertEquals("token", response.token());
  }
}
