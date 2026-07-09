package org.duckdns.massemiso.todo_list_api.controller;

import org.duckdns.massemiso.todo_list_api.dto.AuthResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/todos")
public class TodoController {

  @GetMapping("/{id}")
  public ResponseEntity<String> getTodoById(@PathVariable Long id) {
    return ResponseEntity.ok("YEY YOU MADE IT");
  }

}
