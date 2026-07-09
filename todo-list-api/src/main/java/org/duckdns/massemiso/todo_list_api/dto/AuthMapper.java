package org.duckdns.massemiso.todo_list_api.dto;

import org.duckdns.massemiso.todo_list_api.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

  public User toUser(AuthRequestDto requestDto) {
    return User.builder()
        .name(requestDto.name())
        .email(requestDto.email())
        .password(requestDto.password())
        .build();
  }

  public AuthResponseDto toResponse() {
    return new AuthResponseDto("SAMPLE_TOKEN");
  }
}
