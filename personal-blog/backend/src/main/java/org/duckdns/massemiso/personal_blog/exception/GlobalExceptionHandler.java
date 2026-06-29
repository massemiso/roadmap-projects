package org.duckdns.massemiso.personal_blog.exception;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  private String handleGenericError(String msg, Exception e, Model model){
    model.addAttribute("error", msg);
    log.error(msg, e);
    return "error";
  }

  @ExceptionHandler(IOException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleIOException(IOException e, Model model) {
    return handleGenericError("IO Error", e, model);
  }

  @ExceptionHandler(FileNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public String handleFileNotFoundException(FileNotFoundException e, Model model) {
    return handleGenericError("File not found", e, model);
  }

  @ExceptionHandler(NoSuchFileException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public String handleNoSuchFileException(NoSuchFileException e, Model model) {
    return handleGenericError("File not found", e, model);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
      Model model) {
    return handleGenericError("Validation error", e, model);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleGeneralException(Exception e, Model model) {
    return handleGenericError("Unhandled error", e, model);
  }

  @ExceptionHandler(NoSuchElementException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public String handleNotFound(NoSuchElementException e, Model model) {
    return handleGenericError("Article not found", e, model);
  }

}
