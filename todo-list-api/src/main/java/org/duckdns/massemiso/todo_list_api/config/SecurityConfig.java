package org.duckdns.massemiso.todo_list_api.config;

import org.duckdns.massemiso.todo_list_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final String[] AUTH_WHITELIST = {
      "/login", "/register"
  };

  private final JwtTokenProvider jwtTokenProvider;
  private final UserService userService;

  @Autowired
  public SecurityConfig(
      UserService userService,
      JwtTokenProvider jwtTokenProvider) {
    this.userService = userService;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(auth -> {
          auth.requestMatchers(AUTH_WHITELIST).permitAll();
          auth.anyRequest().authenticated();
        })
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userService),
            UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider(
      PasswordEncoder passwordEncoder,
      UserService userService
  ) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
