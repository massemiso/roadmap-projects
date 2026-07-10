package org.duckdns.massemiso.todo_list_api.dto;

import org.duckdns.massemiso.todo_list_api.entity.Todo;
import org.duckdns.massemiso.todo_list_api.entity.User;
import org.springframework.stereotype.Component;

@Component
public class TodoMapper {

  public Todo toEntity(TodoRequestDto requestDto, User userFromJwtToken) {
    return Todo.builder()
        .title(requestDto.title())
        .description(requestDto.description())
        .completed(requestDto.completed())
        .user(userFromJwtToken)
        .build();
  }

  public TodoResponseDto toDto(Todo todo) {
    return new TodoResponseDto(
        todo.getId(),
        todo.getTitle(),
        todo.getDescription(),
        todo.getCompleted()
    );
  }
}
