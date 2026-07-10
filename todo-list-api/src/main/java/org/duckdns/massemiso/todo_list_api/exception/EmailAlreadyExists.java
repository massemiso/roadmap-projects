package org.duckdns.massemiso.todo_list_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExists extends RuntimeException{

  public EmailAlreadyExists(String email) {
    super(String.format("Email already exists: %s", email));
  }
}
