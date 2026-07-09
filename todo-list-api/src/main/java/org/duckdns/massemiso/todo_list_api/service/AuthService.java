package org.duckdns.massemiso.todo_list_api.service;

import lombok.extern.slf4j.Slf4j;
import org.duckdns.massemiso.todo_list_api.repository.UserRepository;
import org.duckdns.massemiso.todo_list_api.dto.AuthMapper;
import org.duckdns.massemiso.todo_list_api.dto.AuthRequestDto;
import org.duckdns.massemiso.todo_list_api.dto.AuthResponseDto;
import org.duckdns.massemiso.todo_list_api.entity.User;
import org.duckdns.massemiso.todo_list_api.exception.EmailAlreadyExists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

  private final UserRepository userRepository;
  private final UserService userService;
  private final AuthMapper authMapper;
  @Autowired
  public AuthService(UserRepository userRepository, UserService userService, AuthMapper authMapper) {
    this.userRepository = userRepository;
    this.userService = userService;
    this.authMapper = authMapper;
  }

  public AuthResponseDto register(AuthRequestDto requestDto) {
    log.info("Registering user: {}", requestDto);

    if (userRepository.findByEmail(requestDto.email()).isPresent()) {
      throw new EmailAlreadyExists(requestDto.email());
    }

    User user = authMapper.toUser(requestDto);
    user = userRepository.save(user);
    log.info("Successfully registered user: {}", user);

    return login(requestDto);
  }

  public AuthResponseDto login(AuthRequestDto requestDto) {
    log.info("Logining user: {}", requestDto);

    UserDetails user = userService.loadUserByUsername(requestDto.email());
    AuthResponseDto responseDto = authMapper.toResponse();

    log.info("Successfully logged in user {} with token: {}", user, responseDto);
    return responseDto;
  }

}
