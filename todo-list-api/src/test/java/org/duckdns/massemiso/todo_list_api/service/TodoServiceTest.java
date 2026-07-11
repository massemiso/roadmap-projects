package org.duckdns.massemiso.todo_list_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import org.duckdns.massemiso.todo_list_api.dto.TodoFilterDto;
import org.duckdns.massemiso.todo_list_api.dto.TodoMapper;
import org.duckdns.massemiso.todo_list_api.dto.TodoRequestDto;
import org.duckdns.massemiso.todo_list_api.dto.TodoResponseDto;
import org.duckdns.massemiso.todo_list_api.entity.Todo;
import org.duckdns.massemiso.todo_list_api.entity.User;
import org.duckdns.massemiso.todo_list_api.exception.EmailNotFoundException;
import org.duckdns.massemiso.todo_list_api.exception.TodoIdNotFoundException;
import org.duckdns.massemiso.todo_list_api.repository.TodoRepository;
import org.duckdns.massemiso.todo_list_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

  @Mock
  private TodoRepository todoRepository;
  @Mock
  private TodoMapper todoMapper;
  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private TodoService todoService;

  @BeforeEach
  void setUp() {
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("user@example.com");
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  void create_ShouldCreateTodo_WhenValidRequest() {
    TodoRequestDto requestDto = new TodoRequestDto("Title", "Desc", false);
    User user = User.builder().email("user@example.com").build();
    Todo todo = Todo.builder().user(user).title("Title").build();

    when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
    when(todoMapper.toEntity(requestDto, user)).thenReturn(todo);
    when(todoRepository.save(todo)).thenReturn(todo);
    when(todoMapper.toDto(todo)).thenReturn(new TodoResponseDto(1L, "Title", "Desc", false));

    TodoResponseDto result = todoService.create(requestDto);

    assertNotNull(result);
    assertEquals("Title", result.title());
    verify(todoRepository).save(todo);
  }

  @Test
  void create_ShouldThrowEmailNotFound_WhenUserDoesNotExist() {
    TodoRequestDto requestDto = new TodoRequestDto("Title", "Desc", false);
    when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

    assertThrows(EmailNotFoundException.class, () -> todoService.create(requestDto));
  }

  @Test
  void findById_ShouldReturnTodo_WhenTodoExistsAndOwned() {

    Todo todo = Todo.builder().build();
    TodoResponseDto response = new TodoResponseDto(1L, "Title", "Desc", false);

    when(todoRepository.findByUser_EmailAndId("user@example.com", 1L)).thenReturn(
        Optional.of(todo));
    when(todoMapper.toDto(todo)).thenReturn(response);

    TodoResponseDto result = todoService.findById(1L);

    assertNotNull(result);
    assertEquals(1L, result.id());
  }

  @Test
  void findById_ShouldThrowException_WhenTodoNotFoundOrNotOwned() {
    when(todoRepository.findByUser_EmailAndId("user@example.com", 1L)).thenReturn(Optional.empty());

    assertThrows(TodoIdNotFoundException.class, () -> todoService.findById(1L));
  }

  @Test
  @SuppressWarnings("unchecked")
  void findAll_ShouldReturnPage_WhenCalled() {
    TodoFilterDto filter = new TodoFilterDto("Title", null, null);
    Pageable pageable = mock(Pageable.class);
    Page<Todo> page = new PageImpl<>(Collections.singletonList(Todo.builder().build()));

    when(todoRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
    when(todoMapper.toDto(any(Todo.class))).thenReturn(
        new TodoResponseDto(1L, "Title", "Desc", false));

    Page<TodoResponseDto> result = todoService.findAll(filter, pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(todoRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void update_ShouldUpdateAndReturnTodo_WhenTodoExistsAndOwned() {
    Todo todo = mock(Todo.class);
    TodoRequestDto request = new TodoRequestDto("New Title", "New Desc", true);
    TodoResponseDto response = new TodoResponseDto(1L, "New Title", "New Desc", true);

    when(todoRepository.findByUser_EmailAndId("user@example.com", 1L)).thenReturn(
        Optional.of(todo));
    when(todoMapper.toDto(todo)).thenReturn(response);

    TodoResponseDto result = todoService.update(1L, request);

    verify(todo).update("New Title", "New Desc", true);
    assertEquals("New Title", result.title());
  }

  @Test
  void update_ShouldThrowException_WhenTodoNotFoundOrNotOwned() {
    TodoRequestDto request = new TodoRequestDto("T", "D", false);
    when(todoRepository.findByUser_EmailAndId("user@example.com", 1L)).thenReturn(Optional.empty());

    assertThrows(TodoIdNotFoundException.class, () -> todoService.update(1L, request));
  }

  @Test
  void delete_ShouldDelete_WhenTodoExistsAndOwned() {
    Todo todo = Todo.builder().build();
    when(todoRepository.findByUser_EmailAndId("user@example.com", 1L)).thenReturn(
        Optional.of(todo));

    todoService.delete(1L);

    verify(todoRepository).delete(todo);
  }

  @Test
  void delete_ShouldThrowException_WhenTodoNotFoundOrNotOwned() {
    when(todoRepository.findByUser_EmailAndId("user@example.com", 1L)).thenReturn(Optional.empty());

    assertThrows(TodoIdNotFoundException.class, () -> todoService.delete(1L));
  }
}
