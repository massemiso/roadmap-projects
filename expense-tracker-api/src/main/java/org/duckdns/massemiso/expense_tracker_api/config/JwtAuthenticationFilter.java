package org.duckdns.massemiso.expense_tracker_api.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.duckdns.massemiso.expense_tracker_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.ott.OneTimeTokenAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider provider;
  private final UserService userService;

  @Autowired
  public JwtAuthenticationFilter(JwtTokenProvider provider, UserService userService) {
    this.provider = provider;
    this.userService = userService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      log.debug("JWT token found, validating...");
      if (provider.validateToken(token)) {
        log.debug("Token validated successfully.");
        String username = provider.getUser(token);
        log.debug("Loading user details for: {}", username);
        var userDetails = userService.loadUserByUsername(username);

        var authToken = new UsernamePasswordAuthenticationToken(userDetails,
            null,
            userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.debug("Authentication set in context for user: {}", username);
      } else{
        log.debug("Token validation failed.");
      }
    } else{
      log.debug("No JWT token found in request headers.");
    }
    filterChain.doFilter(request, response);
  }
}
