package org.duckdns.massemiso.expense_tracker_api.exception;

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

@Component
@Slf4j
public class AuthExceptionHandler implements
    AuthenticationEntryPoint {

  private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  // Authentication exceptions
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authenticationException)
      throws IOException, ServletException {
    String ipAddress = request.getRemoteAddr();
    String userAgent = request.getHeader("User-Agent");
    String logMsg = String.format("401 UNAUTHORIZED: User [IP %s, Agent %s] tried to make a %s on %s",
        ipAddress, userAgent, request.getMethod(), request.getRequestURI());
    String msg = String.format("Authentication is required to perform a %s on %s",
        request.getMethod(), request.getRequestURI());

    this.writeErrorResponse(
        response,
        HttpStatus.UNAUTHORIZED,
        authenticationException,
        logMsg,
        msg);
  }

  private void writeErrorResponse(
      HttpServletResponse response,
      HttpStatus status,
      Exception ex,
      String logMsg,
      String msg
  ) throws IOException {
    log.warn(logMsg, ex);
    ErrorResponse errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        status.value(),
        msg,
        List.of(ex.getMessage())
    );
    response.setContentType("application/json");
    response.setStatus(status.value());
    response.getWriter().write(mapper.writeValueAsString(errorResponse));
  }
}
