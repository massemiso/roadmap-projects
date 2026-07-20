package org.duckdns.massemiso.expense_tracker_api.dto;

import org.duckdns.massemiso.expense_tracker_api.model.UserEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {


  public AuthResponseDto toResponse(String token){
    return new AuthResponseDto(token);
  }

  public UserEntity toEntity(AuthRequestDto authRequestDto, String passwordEncoded) {
    return UserEntity.builder()
        .username(authRequestDto.username())
        .password(passwordEncoded)
        .build();
  }
}
