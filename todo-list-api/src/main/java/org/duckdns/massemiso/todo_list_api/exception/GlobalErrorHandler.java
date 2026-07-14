package org.duckdns.massemiso.todo_list_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {

  private ResponseEntity<ErrorResponse> helperHandler(
      String logMsg,
      Exception ex,
      HttpStatus status,
      String msg) {
    log.warn(logMsg, ex);
    ErrorResponse errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        status.value(),
        msg,
        List.of(ex.getMessage())
    );
    return ResponseEntity.status(status).body(errorResponse);
  }

  // Authorization exceptions
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(HttpServletRequest request,
      AccessDeniedException ex) {
    String apiMsg = String.format(
        "You do not have the required permissions to perform a %s on %s", request.getMethod(),
        request.getRequestURI());
    String logMsg = String.format(
        "403 FORBIDDEN: User '%s' doesn't have the required permissions to perform a %s on %s",
        request.getRemoteUser(), request.getMethod(), request.getRequestURI());
    return helperHandler(
        logMsg,
        ex,
        HttpStatus.FORBIDDEN,
        apiMsg
    );
  }

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
    return this.helperHandler(
        "401 UNAUTHORIZED: Invalid credentials",
        ex,
        HttpStatus.UNAUTHORIZED,
        "Invalid credentials"
    );
  }

  @ExceptionHandler(EmailAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
    return this.helperHandler(
        "409 CONFLICT: Email already exists",
        ex,
        HttpStatus.CONFLICT,
        "Email already exists"
    );
  }

  @ExceptionHandler(EmailNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorResponse> handleEmailNotFound(EmailNotFoundException ex) {
    return this.helperHandler(
        "404 NOT FOUND: Email not found",
        ex,
        HttpStatus.NOT_FOUND,
        "Email not found"
    );
  }

  @ExceptionHandler(TodoIdNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorResponse> handleTodoIdNotFound(TodoIdNotFoundException ex) {
    return this.helperHandler(
        "404 NOT FOUND: Todo ID not found",
        ex,
        HttpStatus.NOT_FOUND,
        "Todo ID not found"
    );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
    log.warn("400 BAD REQUEST: Validation failed", ex);
    List<String> details = ex.getBindingResult().getFieldErrors().stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .toList();
    ErrorResponse error = new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.BAD_REQUEST.value(),
        "Validation Failed",
        details
    );
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(RateExceededException.class)
  @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
  public ResponseEntity<ErrorResponse> handleRateExceededException(RateExceededException ex) {
    return this.helperHandler(
        "429 TOO MANY REQUESTS: Rate limit exceeded",
        ex,
        HttpStatus.TOO_MANY_REQUESTS,
        "Rate limit exceeded"
    );
  }
}
