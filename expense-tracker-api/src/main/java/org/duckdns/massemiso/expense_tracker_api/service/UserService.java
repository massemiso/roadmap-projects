package org.duckdns.massemiso.expense_tracker_api.service;

import lombok.extern.slf4j.Slf4j;
import org.duckdns.massemiso.expense_tracker_api.model.UserEntity;
import org.duckdns.massemiso.expense_tracker_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository){
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.info("Loading user by username: {}", username);
    UserEntity user = userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));
    log.info("Found user: {}", user);
    return User.builder()
        .username(user.getUsername())
        .password(user.getPassword())
        .build();
  }
}
