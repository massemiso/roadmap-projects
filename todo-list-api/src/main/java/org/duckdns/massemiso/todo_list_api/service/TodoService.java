package org.duckdns.massemiso.todo_list_api.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.duckdns.massemiso.todo_list_api.config.JwtTokenProvider;
import org.duckdns.massemiso.todo_list_api.dto.TodoMapper;
import org.duckdns.massemiso.todo_list_api.dto.TodoRequestDto;
import org.duckdns.massemiso.todo_list_api.dto.TodoResponseDto;
import org.duckdns.massemiso.todo_list_api.entity.Todo;
import org.duckdns.massemiso.todo_list_api.entity.User;
import org.duckdns.massemiso.todo_list_api.repository.TodoRepository;
import org.duckdns.massemiso.todo_list_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TodoService {

  private final TodoRepository todoRepository;
  private final TodoMapper todoMapper;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;

  @Autowired
  public TodoService(
      TodoRepository todoRepository,
      TodoMapper todoMapper,
      JwtTokenProvider jwtTokenProvider,
      UserRepository userRepository) {
    this.todoRepository = todoRepository;
    this.todoMapper = todoMapper;
    this.jwtTokenProvider = jwtTokenProvider;
    this.userRepository = userRepository;
  }

  private User getUserFromJwtToken(String jwtToken) {
   return userRepository.findByEmail(jwtTokenProvider.getEmail(jwtToken)).orElseThrow();
  }

  @Transactional
  public TodoResponseDto create(TodoRequestDto requestDto, String jwtToken) {
    log.info("Creating new todo task {}", requestDto);

    Todo todo = todoMapper.toEntity(requestDto, this.getUserFromJwtToken(jwtToken));
    todo = todoRepository.save(todo);
    TodoResponseDto responseDto = todoMapper.toDto(todo);

    log.info("Successfully created todo task {}", responseDto);
    return responseDto;
  }

  public TodoResponseDto findById(Long id) {
    log.info("Trying to find task {}", id);

    Todo todo = todoRepository.findById(id).orElseThrow();
    TodoResponseDto responseDto = todoMapper.toDto(todo);

    log.info("Successfully found todo task {}", responseDto);
    return responseDto;
  }

  public List<TodoResponseDto> findAll() {
    log.info("Trying to find all tasks");

    List<Todo> todos = todoRepository.findAll();
    List<TodoResponseDto> dtos = todos.stream().map(todoMapper::toDto).toList();

    log.info("Successfully found all tasks {}", dtos);
    return dtos;
  }

  @Transactional
  public TodoResponseDto update(Long id, TodoRequestDto requestDto) {
    log.info("Trying to update task {} with {}", id, requestDto);

    Todo todo =  todoRepository.findById(id).orElseThrow();
    todo.update(requestDto.title(), requestDto.description(), requestDto.completed());
    TodoResponseDto responseDto = todoMapper.toDto(todo);

    log.info("Successfully updated task {}", responseDto);
    return responseDto;
  }

  @Transactional
  public void delete(Long id) {
    log.info("Trying to delete task {}", id);

    Todo todo =  todoRepository.findById(id).orElseThrow();
    todoRepository.delete(todo);

    log.info("Successfully deleted task {}", id);
  }
}
