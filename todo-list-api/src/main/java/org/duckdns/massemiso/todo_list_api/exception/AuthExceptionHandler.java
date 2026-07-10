package org.duckdns.massemiso.todo_list_api.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthExceptionHandler implements AuthenticationEntryPoint {

  // Authentication exceptions
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authenticationException)
      throws IOException, ServletException {
    String msg = String.format("Authentication is required to perform a %s on %s",
        request.getMethod(), request.getRequestURI());
    ErrorResponse errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.UNAUTHORIZED.value(),
        msg,
        List.of(msg)
    );

    String ipAddress = request.getRemoteAddr();
    String userAgent = request.getHeader("User-Agent");
    log.warn(
        "401 UNAUTHORIZED: User [IP {}, Agent {}] tried to make a {} on {}",
        ipAddress,
        userAgent,
        request.getMethod(),
        request.getRequestURI());

    response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    response.getWriter().write(mapper.writeValueAsString(errorResponse));
  }

}
