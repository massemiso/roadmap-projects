package org.duckdns.massemiso.todo_list_api.service;

import org.duckdns.massemiso.todo_list_api.config.JwtTokenProvider;
import org.duckdns.massemiso.todo_list_api.dto.AuthMapper;
import org.duckdns.massemiso.todo_list_api.dto.AuthRequestDto;
import org.duckdns.massemiso.todo_list_api.dto.AuthResponseDto;
import org.duckdns.massemiso.todo_list_api.entity.RefreshToken;
import org.duckdns.massemiso.todo_list_api.entity.User;
import org.duckdns.massemiso.todo_list_api.exception.EmailAlreadyExistsException;
import org.duckdns.massemiso.todo_list_api.repository.RefreshTokenRepository;
import org.duckdns.massemiso.todo_list_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserService userService;
  @Mock
  private AuthMapper authMapper;
  @Mock
  private JwtTokenProvider jwtTokenProvider;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @InjectMocks
  private AuthService authService;

  @Test
  void register_ShouldReturnToken_WhenValidRequest() {
    AuthRequestDto request = new AuthRequestDto("Name", "email@ex.com", "pass");
    User user = User.builder().name("Name").email("email@ex.com").build();
    RefreshToken refreshToken = RefreshToken.builder().token("rt").build();

    when(userRepository.findByEmail("email@ex.com")).thenReturn(Optional.empty()).thenReturn(Optional.of(user));
    when(passwordEncoder.encode("pass")).thenReturn("encoded");
    when(authMapper.toUser(request, "encoded")).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);
    when(passwordEncoder.matches("pass", user.getPassword())).thenReturn(true);

    // Mocking the internal login call
    when(jwtTokenProvider.createToken("email@ex.com", 15*60000)).thenReturn("token");
    when(refreshTokenRepository.findByUser(any())).thenReturn(Optional.empty());
    when(authMapper.toResponse(eq("token"), any())).thenReturn(new AuthResponseDto("token", "rt"));

    AuthResponseDto result = authService.register(request);

    assertEquals("token", result.accessToken());
    verify(userRepository, times(2)).findByEmail("email@ex.com");
    verify(userRepository).save(user);
  }

  @Test
  void register_ShouldThrowException_WhenEmailExists() {
    AuthRequestDto request = new AuthRequestDto("Name", "email@ex.com", "pass");
    when(userRepository.findByEmail("email@ex.com")).thenReturn(
        Optional.of(User.builder().build()));

    assertThrows(EmailAlreadyExistsException.class, () -> authService.register(request));
  }

  @Test
  void login_ShouldReturnToken_WhenValid() {
    AuthRequestDto request = new AuthRequestDto("Name", "email@ex.com", "pass");
    User user = User.builder().email("email@ex.com").build();

    when(userRepository.findByEmail("email@ex.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("pass", user.getPassword())).thenReturn(true);
    when(jwtTokenProvider.createToken("email@ex.com", 15*60000)).thenReturn("token");
    when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.empty());
    when(authMapper.toResponse(eq("token"), any())).thenReturn(new AuthResponseDto("token", "rt"));

    AuthResponseDto result = authService.login(request);
    assertEquals("token", result.accessToken());
  }

  @Test
  void login_GivenBadPassword_ShouldReturn401Unauthorized() {
    AuthRequestDto request = new AuthRequestDto("Name", "email@ex.com", "pass");
    User user = User.builder().email("email@ex.com").build();

    when(userRepository.findByEmail("email@ex.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("pass", user.getPassword())).thenReturn(false);

    assertThrows(BadCredentialsException.class, () -> authService.login(request));

    verify(userRepository).findByEmail("email@ex.com");
    verify(passwordEncoder).matches("pass", user.getPassword());
    verify(jwtTokenProvider, never()).createToken(anyString(), anyLong());
    verify(refreshTokenRepository, never()).findByUser(any());
    verify(authMapper, never()).toResponse(any(), any());
  }
}
