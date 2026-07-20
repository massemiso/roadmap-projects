package org.duckdns.massemiso.expense_tracker_api.exception;

import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

  @ExceptionHandler(ExpenseNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleExpenseNotFoundException(
      ExpenseNotFoundException ex) {
    return this.helperHandler(
        "404 NOT FOUND: Expense not found",
        ex,
        HttpStatus.NOT_FOUND,
        "Expense not found"
    );
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
      UsernameNotFoundException ex) {
    return this.helperHandler(
        "404 NOT FOUND: Username not found",
        ex,
        HttpStatus.NOT_FOUND,
        "Username not found"
    );
  }

  @ExceptionHandler(UserEntityAlreadyExists.class)
  public ResponseEntity<ErrorResponse> handleUserEntityAlreadyExists(
      UserEntityAlreadyExists ex) {
    return this.helperHandler(
        "409 CONFLICT: User already exists",
        ex,
        HttpStatus.CONFLICT,
        "User already exists"
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
}
