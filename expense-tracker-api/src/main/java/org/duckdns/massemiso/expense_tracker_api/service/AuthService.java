package org.duckdns.massemiso.expense_tracker_api.service;

import lombok.extern.slf4j.Slf4j;
import org.duckdns.massemiso.expense_tracker_api.config.JwtTokenProvider;
import org.duckdns.massemiso.expense_tracker_api.dto.AuthMapper;
import org.duckdns.massemiso.expense_tracker_api.dto.AuthRequestDto;
import org.duckdns.massemiso.expense_tracker_api.dto.AuthResponseDto;
import org.duckdns.massemiso.expense_tracker_api.exception.UserEntityAlreadyExists;
import org.duckdns.massemiso.expense_tracker_api.model.UserEntity;
import org.duckdns.massemiso.expense_tracker_api.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

  private final UserService userService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthMapper authMapper;
  private final JwtTokenProvider jwtTokenProvider;

  public AuthService(
      UserService userService,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthMapper authMapper,
      JwtTokenProvider jwtTokenProvider
  ) {
    this.userService = userService;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authMapper = authMapper;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  public AuthResponseDto login(AuthRequestDto authRequestDto) {
    log.info("Trying to authenticate user {}", authRequestDto);

    UserDetails user = userService.loadUserByUsername(authRequestDto.username());
    if (!passwordEncoder.matches(authRequestDto.password(), user.getPassword())){
      throw new BadCredentialsException("Wrong password");
    }
    AuthResponseDto responseDto = authMapper.toResponse(jwtTokenProvider
        .createToken(user.getUsername()));

    log.info("Authenticated user '{}', tokens = {}", user.getUsername(), responseDto);
    return responseDto;
  }

  public AuthResponseDto register(AuthRequestDto authRequestDto) {
    log.info("Trying to register user {}", authRequestDto);

    if(userRepository.findByUsername(authRequestDto.username()).isPresent()){
      throw new UserEntityAlreadyExists(authRequestDto.username());
    }

    UserEntity user = authMapper.toEntity(authRequestDto,
        passwordEncoder.encode(authRequestDto.password()));
    user = userRepository.save(user);
    AuthResponseDto responseDto = authMapper.toResponse(jwtTokenProvider
        .createToken(user.getUsername()));

    log.info("Registered user {}, tokens = {}", user.getUsername(), responseDto);
    return responseDto;
  }
}
