package org.duckdns.massemiso.todo_list_api.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.duckdns.massemiso.todo_list_api.dto.TodoFilterDto;
import org.duckdns.massemiso.todo_list_api.dto.TodoMapper;
import org.duckdns.massemiso.todo_list_api.dto.TodoRequestDto;
import org.duckdns.massemiso.todo_list_api.dto.TodoResponseDto;
import org.duckdns.massemiso.todo_list_api.entity.Todo;
import org.duckdns.massemiso.todo_list_api.entity.User;
import org.duckdns.massemiso.todo_list_api.exception.EmailNotFoundException;
import org.duckdns.massemiso.todo_list_api.exception.TodoIdNotFoundException;
import org.duckdns.massemiso.todo_list_api.repository.TodoRepository;
import org.duckdns.massemiso.todo_list_api.repository.TodoSpecifications;
import org.duckdns.massemiso.todo_list_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TodoService {

  private final TodoRepository todoRepository;
  private final TodoMapper todoMapper;
  private final UserRepository userRepository;

  @Autowired
  public TodoService(
      TodoRepository todoRepository,
      TodoMapper todoMapper,
      UserRepository userRepository) {
    this.todoRepository = todoRepository;
    this.todoMapper = todoMapper;
    this.userRepository = userRepository;
  }

  private String getUserEmail(){
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  private User getUser() {
   return userRepository
       .findByEmail(this.getUserEmail())
       .orElseThrow(EmailNotFoundException::new);
  }

  @Transactional
  public TodoResponseDto create(TodoRequestDto requestDto) {
    log.info("Creating new todo task {}", requestDto);

    Todo todo = todoMapper.toEntity(requestDto, this.getUser());
    todo = todoRepository.save(todo);
    TodoResponseDto responseDto = todoMapper.toDto(todo);

    log.info("Successfully created todo task {}", responseDto);
    return responseDto;
  }

  public TodoResponseDto findById(Long id) {
    log.info("Trying to find task {}", id);

    String email = this.getUserEmail();
    Todo todo = todoRepository
        .findByUser_EmailAndId(email, id)
        .orElseThrow(() -> new TodoIdNotFoundException(id)); // If not found, they don't own it or it doesn't exist

    TodoResponseDto responseDto = todoMapper.toDto(todo);

    log.info("Successfully found todo task {}", responseDto);
    return responseDto;
  }

  public Page<TodoResponseDto> findAll(TodoFilterDto todoFilterDto, Pageable pageable) {
    log.info("Trying to find all tasks");

    String email = this.getUserEmail();
    Specification<Todo> spec = todoFilterDto.getSpecification();
    spec = spec.and(TodoSpecifications.ownedBy(email));
    
    Page<TodoResponseDto> page = todoRepository
        .findAll(spec, pageable)
        .map(todoMapper::toDto);

    log.info("Successfully found {} tasks {}", page.getTotalElements(), page);
    return page;
  }

  @Transactional
  public TodoResponseDto update(Long id, TodoRequestDto requestDto) {
    log.info("Trying to update task {} with {}", id, requestDto);

    String email = this.getUserEmail();
    Todo todo = todoRepository
        .findByUser_EmailAndId(email, id)
        .orElseThrow(() -> new TodoIdNotFoundException(id));
    todo.update(requestDto.title(), requestDto.description(), requestDto.completed());
    TodoResponseDto responseDto = todoMapper.toDto(todo);

    log.info("Successfully updated task {}", responseDto);
    return responseDto;
  }

  @Transactional
  public void delete(Long id) {
    log.info("Trying to delete task {}", id);

    String email = this.getUserEmail();
    Todo todo = todoRepository
        .findByUser_EmailAndId(email, id)
        .orElseThrow(() -> new TodoIdNotFoundException(id));
    todoRepository.delete(todo);

    log.info("Successfully deleted task {}", id);
  }
}
