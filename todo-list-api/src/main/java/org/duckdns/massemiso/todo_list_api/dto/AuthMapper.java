package org.duckdns.massemiso.todo_list_api.dto;

import org.duckdns.massemiso.todo_list_api.entity.RefreshToken;
import org.duckdns.massemiso.todo_list_api.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

  public User toUser(AuthRequestDto requestDto, String encodedPassword) {
    return User.builder()
        .name(requestDto.name())
        .email(requestDto.email())
        .password(encodedPassword)
        .build();
  }

  public AuthResponseDto toResponse(String accessToken, RefreshToken refreshToken) {
    return new AuthResponseDto(accessToken, refreshToken.getToken());
  }
}
