package org.duckdns.massemiso.expense_tracker_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.duckdns.massemiso.expense_tracker_api.model.UserEntity;
import org.duckdns.massemiso.expense_tracker_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  @Test
  void loadUserByUsername_ShouldReturnUserDetails() {
    UserEntity userEntity = UserEntity.builder()
        .username("testuser")
        .password("password")
        .build();
    
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(userEntity));

    UserDetails userDetails = userService.loadUserByUsername("testuser");

    assertEquals("testuser", userDetails.getUsername());
    assertEquals("password", userDetails.getPassword());
  }

  @Test
  void loadUserByUsername_ShouldThrowException() {
    when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("unknown"));
  }
}
