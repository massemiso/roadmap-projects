package org.duckdns.massemiso.expense_tracker_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.duckdns.massemiso.expense_tracker_api.config.JwtTokenProvider;
import org.duckdns.massemiso.expense_tracker_api.dto.AuthMapper;
import org.duckdns.massemiso.expense_tracker_api.dto.AuthRequestDto;
import org.duckdns.massemiso.expense_tracker_api.dto.AuthResponseDto;
import org.duckdns.massemiso.expense_tracker_api.exception.UserEntityAlreadyExists;
import org.duckdns.massemiso.expense_tracker_api.model.UserEntity;
import org.duckdns.massemiso.expense_tracker_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserService userService;
  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private AuthMapper authMapper;
  @Mock private JwtTokenProvider jwtTokenProvider;

  @InjectMocks private AuthService authService;

  @Test
  void login_ShouldReturnToken() {
    AuthRequestDto requestDto = new AuthRequestDto("user", "password");
    UserDetails userDetails = User.withUsername("user").password("encodedPassword").roles("USER").build();
    
    when(userService.loadUserByUsername("user")).thenReturn(userDetails);
    when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
    when(jwtTokenProvider.createToken("user")).thenReturn("token");
    when(authMapper.toResponse("token")).thenReturn(new AuthResponseDto("token"));

    AuthResponseDto response = authService.login(requestDto);

    assertEquals("token", response.token());
  }

  @Test
  void login_ShouldThrowBadCredentialsException() {
    AuthRequestDto requestDto = new AuthRequestDto("user", "wrongPassword");
    UserDetails userDetails = User.withUsername("user").password("encodedPassword").roles("USER").build();
    
    when(userService.loadUserByUsername("user")).thenReturn(userDetails);
    when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

    assertThrows(BadCredentialsException.class, () -> authService.login(requestDto));
  }

  @Test
  void register_ShouldReturnToken() {
    AuthRequestDto requestDto = new AuthRequestDto("user", "password");
    UserEntity userEntity = UserEntity.builder().username("user").password("encoded").build();
    
    when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("password")).thenReturn("encoded");
    when(authMapper.toEntity(requestDto, "encoded")).thenReturn(userEntity);
    when(userRepository.save(userEntity)).thenReturn(userEntity);
    when(jwtTokenProvider.createToken("user")).thenReturn("token");
    when(authMapper.toResponse("token")).thenReturn(new AuthResponseDto("token"));

    AuthResponseDto response = authService.register(requestDto);

    assertEquals("token", response.token());
    verify(userRepository).save(userEntity);
  }

  @Test
  void register_ShouldThrowUserEntityAlreadyExists() {
    AuthRequestDto requestDto = new AuthRequestDto("user", "password");
    
    when(userRepository.findByUsername("user")).thenReturn(Optional.of(UserEntity.builder().build()));

    assertThrows(UserEntityAlreadyExists.class, () -> authService.register(requestDto));
  }
}
