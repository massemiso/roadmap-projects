package org.duckdns.massemiso.todo_list_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TodoIdNotFoundException extends RuntimeException {

  public TodoIdNotFoundException(Long id) {
    super(String.format("To-do with ID %d not found", id));
  }
}
