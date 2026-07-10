package org.duckdns.massemiso.todo_list_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.duckdns.massemiso.todo_list_api.config.JwtTokenProvider;
import org.duckdns.massemiso.todo_list_api.dto.AuthMapper;
import org.duckdns.massemiso.todo_list_api.dto.AuthRequestDto;
import org.duckdns.massemiso.todo_list_api.dto.AuthResponseDto;
import org.duckdns.massemiso.todo_list_api.entity.User;
import org.duckdns.massemiso.todo_list_api.exception.EmailAlreadyExists;
import org.duckdns.massemiso.todo_list_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

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

  @InjectMocks
  private AuthService authService;

  @Test
  void register_ShouldReturnToken_WhenValidRequest() {
    AuthRequestDto request = new AuthRequestDto("Name", "email@ex.com", "pass");
    User user = User.builder().name("Name").email("email@ex.com").build();

    when(userRepository.findByEmail("email@ex.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("pass")).thenReturn("encoded");
    when(authMapper.toUser(request, "encoded")).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);

    // Mocking the internal login call
    when(userService.loadUserByUsername("email@ex.com")).thenReturn(mock(UserDetails.class));
    when(jwtTokenProvider.createToken("email@ex.com")).thenReturn("token");
    when(authMapper.toResponse("token")).thenReturn(new AuthResponseDto("token"));

    AuthResponseDto result = authService.register(request);

    assertEquals("token", result.token());
    verify(userRepository).save(user);
  }

  @Test
  void register_ShouldThrowException_WhenEmailExists() {
    AuthRequestDto request = new AuthRequestDto("Name", "email@ex.com", "pass");
    when(userRepository.findByEmail("email@ex.com")).thenReturn(
        Optional.of(User.builder().build()));

    assertThrows(EmailAlreadyExists.class, () -> authService.register(request));
  }

  @Test
  void login_ShouldReturnToken_WhenValid() {
    AuthRequestDto request = new AuthRequestDto("Name", "email@ex.com", "pass");
    when(userService.loadUserByUsername("email@ex.com")).thenReturn(mock(UserDetails.class));
    when(jwtTokenProvider.createToken("email@ex.com")).thenReturn("token");
    when(authMapper.toResponse("token")).thenReturn(new AuthResponseDto("token"));

    AuthResponseDto result = authService.login(request);
    assertEquals("token", result.token());
  }
}
