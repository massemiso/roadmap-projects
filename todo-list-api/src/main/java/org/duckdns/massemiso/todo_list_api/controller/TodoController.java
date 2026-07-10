package org.duckdns.massemiso.todo_list_api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import org.duckdns.massemiso.todo_list_api.dto.TodoRequestDto;
import org.duckdns.massemiso.todo_list_api.dto.TodoResponseDto;
import org.duckdns.massemiso.todo_list_api.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/todos")
public class TodoController {

  private final TodoService todoService;

  @Autowired
  public TodoController(TodoService todoService){
    this.todoService = todoService;
  }

  private String extractToken(HttpServletRequest request){
    String token = request.getHeader("Authorization");
    if (token.startsWith("Bearer ")) {
      token = token.substring(7); // Removes "Bearer "
    }
    return token;
  }

  @PostMapping
  public ResponseEntity<TodoResponseDto> create(
      HttpServletRequest request,
      @Valid @RequestBody TodoRequestDto requestDto){
    String jwtToken = this.extractToken(request);
    TodoResponseDto responseDto = this.todoService.create(requestDto, jwtToken);
    return ResponseEntity
        .created(URI.create("/todos/" + responseDto.id()))
        .body(responseDto);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TodoResponseDto> findById(
      @PathVariable Long id) {
    TodoResponseDto responseDto = this.todoService.findById(id);
    return ResponseEntity.ok(responseDto);
  }

  @GetMapping
  public ResponseEntity<Page<TodoResponseDto>> findAll(
      @RequestParam(required = false) String title,
      @RequestParam(required = false) String description,
      @RequestParam(required = false) Boolean completed,
      Pageable pageable) {
    Page<TodoResponseDto> dtos = this.todoService.findAll(title, description, completed, pageable);
    return ResponseEntity.ok(dtos);
  }

  @PutMapping("/{id}")
  public ResponseEntity<TodoResponseDto> update(
      @PathVariable Long id,
      @Valid @RequestBody TodoRequestDto requestDto
  ){
    TodoResponseDto responseDto = this.todoService.update(id, requestDto);
    return ResponseEntity.ok(responseDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id){
    this.todoService.delete(id);
    return ResponseEntity.noContent().build();
  }

}
