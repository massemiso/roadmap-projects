package org.duckdns.massemiso.todo_list_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class RateExceededException extends RuntimeException{

  public RateExceededException(String clientIp) {
    super(String.format("User %s exceeded its quota", clientIp));
  }
}
