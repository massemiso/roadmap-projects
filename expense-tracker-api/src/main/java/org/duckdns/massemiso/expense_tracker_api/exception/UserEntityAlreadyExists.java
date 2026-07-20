package org.duckdns.massemiso.expense_tracker_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserEntityAlreadyExists extends RuntimeException{

  public UserEntityAlreadyExists(String username) {
    super(String.format("User with name '%s' already exists", username));
  }
}
