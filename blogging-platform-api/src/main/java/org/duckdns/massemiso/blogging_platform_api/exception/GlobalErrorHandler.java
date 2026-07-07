package org.duckdns.massemiso.blogging_platform_api.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
    log.warn("Validation error!", ex);

    List<String> details = ex.getBindingResult().getFieldErrors().stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .toList();
    ErrorResponse error = new ErrorResponse(
        LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Validation Failed", details);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(PostNotFoundException.class)
  public ResponseEntity<ErrorResponse> handlePostNotFound(PostNotFoundException ex) {
    log.warn("Post not found!", ex);

    ErrorResponse error = new ErrorResponse(
        LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "Post not found", List.of(ex.getMessage()));
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }


}
