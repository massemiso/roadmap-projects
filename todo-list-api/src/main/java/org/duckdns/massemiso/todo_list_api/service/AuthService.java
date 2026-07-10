package org.duckdns.massemiso.todo_list_api.service;

import lombok.extern.slf4j.Slf4j;
import org.duckdns.massemiso.todo_list_api.config.JwtTokenProvider;
import org.duckdns.massemiso.todo_list_api.dto.AuthMapper;
import org.duckdns.massemiso.todo_list_api.dto.AuthRequestDto;
import org.duckdns.massemiso.todo_list_api.dto.AuthResponseDto;
import org.duckdns.massemiso.todo_list_api.entity.User;
import org.duckdns.massemiso.todo_list_api.exception.EmailAlreadyExists;
import org.duckdns.massemiso.todo_list_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

  private final UserRepository userRepository;
  private final UserService userService;
  private final AuthMapper authMapper;
  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;
  @Autowired
  public AuthService(
      UserRepository userRepository,
      UserService userService,
      AuthMapper authMapper,
      JwtTokenProvider jwtTokenProvider,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.userService = userService;
    this.authMapper = authMapper;
    this.jwtTokenProvider = jwtTokenProvider;
    this.passwordEncoder = passwordEncoder;
  }

  public AuthResponseDto register(AuthRequestDto requestDto) {
    log.info("Registering user: {}", requestDto);

    if (userRepository.findByEmail(requestDto.email()).isPresent()) {
      throw new EmailAlreadyExists(requestDto.email());
    }

    String encodedPassword = passwordEncoder.encode(requestDto.password());
    User user = authMapper.toUser(requestDto, encodedPassword);
    user = userRepository.save(user);
    log.info("Successfully registered user: {}", user);

    return login(requestDto);
  }

  public AuthResponseDto login(AuthRequestDto requestDto) {
    log.info("Logining user: {}", requestDto);

    UserDetails user = userService.loadUserByUsername(requestDto.email());
    String jwtToken = jwtTokenProvider.createToken(requestDto.email());
    AuthResponseDto responseDto = authMapper.toResponse(jwtToken);

    log.info("Successfully logged in user {} with token: {}", user, responseDto);
    return responseDto;
  }

}
