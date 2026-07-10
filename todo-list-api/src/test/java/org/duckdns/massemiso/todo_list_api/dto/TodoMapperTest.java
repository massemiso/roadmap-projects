package org.duckdns.massemiso.todo_list_api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.duckdns.massemiso.todo_list_api.entity.Todo;
import org.duckdns.massemiso.todo_list_api.entity.User;
import org.junit.jupiter.api.Test;

public class TodoMapperTest {

  private final TodoMapper todoMapper = new TodoMapper();

  @Test
  void toEntity_ShouldMapCorrectly() {
    User user = User.builder().build();
    TodoRequestDto request = new TodoRequestDto("Title", "Desc", false);
    Todo todo = todoMapper.toEntity(request, user);

    assertEquals("Title", todo.getTitle());
    assertEquals(user, todo.getUser());
  }

  @Test
  void toDto_ShouldMapCorrectly() {
    Todo todo = Todo.builder().title("Title").build();
    TodoResponseDto dto = todoMapper.toDto(todo);
    assertEquals("Title", dto.title());
  }
}
