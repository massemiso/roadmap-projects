package org.duckdns.massemiso.expense_tracker_api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.duckdns.massemiso.expense_tracker_api.model.UserEntity;
import org.junit.jupiter.api.Test;

class AuthMapperTest {

  private final AuthMapper authMapper = new AuthMapper();

  @Test
  void toResponse_ShouldReturnCorrectDto() {
    String token = "test-token";
    AuthResponseDto response = authMapper.toResponse(token);
    assertEquals(token, response.token());
  }

  @Test
  void toEntity_ShouldReturnCorrectUserEntity() {
    AuthRequestDto requestDto = new AuthRequestDto("username", "password");
    String encodedPassword = "encoded-password";
    UserEntity user = authMapper.toEntity(requestDto, encodedPassword);

    assertEquals("username", user.getUsername());
    assertEquals("encoded-password", user.getPassword());
  }
}
