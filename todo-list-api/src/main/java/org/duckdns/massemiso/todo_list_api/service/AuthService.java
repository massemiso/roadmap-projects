package org.duckdns.massemiso.todo_list_api.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.duckdns.massemiso.todo_list_api.config.JwtTokenProvider;
import org.duckdns.massemiso.todo_list_api.dto.AuthMapper;
import org.duckdns.massemiso.todo_list_api.dto.AuthRequestDto;
import org.duckdns.massemiso.todo_list_api.dto.AuthResponseDto;
import org.duckdns.massemiso.todo_list_api.entity.RefreshToken;
import org.duckdns.massemiso.todo_list_api.entity.User;
import org.duckdns.massemiso.todo_list_api.exception.EmailAlreadyExistsException;
import org.duckdns.massemiso.todo_list_api.exception.EmailNotFoundException;
import org.duckdns.massemiso.todo_list_api.repository.RefreshTokenRepository;
import org.duckdns.massemiso.todo_list_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {

  private final UserRepository userRepository;
  private final UserService userService;
  private final AuthMapper authMapper;
  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;
  private final RefreshTokenRepository refreshTokenRepository;

  @Autowired
  public AuthService(
      UserRepository userRepository,
      UserService userService,
      AuthMapper authMapper,
      JwtTokenProvider jwtTokenProvider,
      PasswordEncoder passwordEncoder,
      RefreshTokenRepository refreshTokenRepository) {
    this.userRepository = userRepository;
    this.userService = userService;
    this.authMapper = authMapper;
    this.jwtTokenProvider = jwtTokenProvider;
    this.passwordEncoder = passwordEncoder;
    this.refreshTokenRepository = refreshTokenRepository;
  }

  public AuthResponseDto register(AuthRequestDto requestDto) {
    log.info("Registering user: {}", requestDto);

    if (userRepository.findByEmail(requestDto.email()).isPresent()) {
      throw new EmailAlreadyExistsException(requestDto.email());
    }

    String encodedPassword = passwordEncoder.encode(requestDto.password());
    User user = authMapper.toUser(requestDto, encodedPassword);
    userRepository.save(user);
    log.info("Successfully registered user: {}", user);

    return login(requestDto);
  }

  @Transactional
  public AuthResponseDto login(AuthRequestDto requestDto) {
    log.info("Logining user: {}", requestDto);

    UserDetails userDetails = userService.loadUserByUsername(requestDto.email());
    User user = userRepository.findByEmail(requestDto.email())
        .orElseThrow(() -> new EmailNotFoundException(requestDto.email()));

    return createTokensForUser(user);
  }

  @Transactional
  public AuthResponseDto refresh(String token) {
    log.info("Refreshing tokens with provided token");

    RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
        .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

    if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      refreshTokenRepository.delete(refreshToken);
      throw new RuntimeException("Refresh token expired");
    }

    User user = refreshToken.getUser();
    refreshTokenRepository.delete(refreshToken); // Rotation: Delete old
    return createTokensForUser(user);
  }

  private AuthResponseDto createTokensForUser(User user) {
    String accessToken = jwtTokenProvider.createToken(user.getEmail(), 15 * 60000);
    String refreshTokenValue = UUID.randomUUID().toString();

    RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
        .orElse(RefreshToken.builder()
            .token(refreshTokenValue)
            .user(user)
            .expiryDate(LocalDateTime.now().plusDays(7))
            .build());

    refreshTokenRepository.save(refreshToken);
    return authMapper.toResponse(accessToken, refreshToken);
  }
}
