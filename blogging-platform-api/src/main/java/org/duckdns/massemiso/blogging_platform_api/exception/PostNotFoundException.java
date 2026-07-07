package org.duckdns.massemiso.blogging_platform_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PostNotFoundException extends RuntimeException {
  public PostNotFoundException(Long id) {
    super(String.format("Post with id %s not found", id));
  }
}
