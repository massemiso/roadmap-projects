package org.duckdns.massemiso.todo_list_api.controller;

import jakarta.validation.Valid;
import java.net.URI;
import org.duckdns.massemiso.todo_list_api.dto.TodoFilterDto;
import org.duckdns.massemiso.todo_list_api.dto.TodoRequestDto;
import org.duckdns.massemiso.todo_list_api.dto.TodoResponseDto;
import org.duckdns.massemiso.todo_list_api.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/todos")
public class TodoController {

  private final TodoService todoService;

  @Autowired
  public TodoController(TodoService todoService){
    this.todoService = todoService;
  }

  @PostMapping
  public ResponseEntity<TodoResponseDto> create(
      @Valid @RequestBody TodoRequestDto requestDto){
    TodoResponseDto responseDto = this.todoService.create(requestDto);
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
      @ModelAttribute TodoFilterDto todoFilterDto,
      Pageable pageable) {
    Page<TodoResponseDto> dtos = this.todoService.findAll(todoFilterDto, pageable);
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
