package org.duckdns.massemiso.todo_list_api.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    String ipAddress = request.getRemoteAddr();
    String userAgent = request.getHeader("User-Agent");
    String logMsg = String.format("401 UNAUTHORIZED: User [IP %s, Agent %s] tried to make a %s on %s",
        ipAddress, userAgent, request.getMethod(), request.getRequestURI());
    String msg = String.format("Authentication is required to perform a %s on %s",
        request.getMethod(), request.getRequestURI());

    ResponseUtil.writeErrorResponse(
        response,
        HttpStatus.UNAUTHORIZED,
        authenticationException,
        logMsg,
        msg);
  }

}
