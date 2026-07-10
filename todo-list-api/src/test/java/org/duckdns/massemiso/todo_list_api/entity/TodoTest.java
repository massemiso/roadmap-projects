package org.duckdns.massemiso.todo_list_api.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TodoTest {

  @Test
  void update_ShouldUpdateFields_WhenValuesAreProvided() {
    Todo todo = Todo.builder().title("Old").description("Old").completed(false).build();
    todo.update("New", "New", true);

    assertEquals("New", todo.getTitle());
    assertEquals("New", todo.getDescription());
    assertTrue(todo.getCompleted());
  }

  @Test
  void update_ShouldIgnoreBlankFields_WhenBlankValuesProvided() {
    Todo todo = Todo.builder().title("Old").description("Old").completed(false).build();
    todo.update(" ", "  ", true);

    assertEquals("Old", todo.getTitle());
    assertEquals("Old", todo.getDescription());
    assertTrue(todo.getCompleted());
  }
}
