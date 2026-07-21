package org.duckdns.massemiso.expense_tracker_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import org.duckdns.massemiso.expense_tracker_api.dto.ExpenseFilterDto;
import org.duckdns.massemiso.expense_tracker_api.dto.ExpenseMapper;
import org.duckdns.massemiso.expense_tracker_api.dto.ExpenseRequestDto;
import org.duckdns.massemiso.expense_tracker_api.dto.ExpenseResponseDto;
import org.duckdns.massemiso.expense_tracker_api.exception.ExpenseNotFoundException;
import org.duckdns.massemiso.expense_tracker_api.model.Category;
import org.duckdns.massemiso.expense_tracker_api.model.Expense;
import org.duckdns.massemiso.expense_tracker_api.model.UserEntity;
import org.duckdns.massemiso.expense_tracker_api.repository.ExpenseRepository;
import org.duckdns.massemiso.expense_tracker_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

  @Mock private ExpenseRepository expenseRepository;
  @Mock private ExpenseMapper expenseMapper;
  @Mock private UserRepository userRepository;
  @Mock private SecurityContext securityContext;
  @Mock private Authentication authentication;

  @InjectMocks private ExpenseService expenseService;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("testuser");
  }

  @Test
  void getById_ShouldReturnExpense() {
    Long id = 1L;
    Expense expense = Expense.builder().build();
    String description = "desc";
    String category = "OTHERS";
    when(expenseRepository.findByUser_UsernameAndId("testuser", id)).thenReturn(Optional.of(expense));
    when(expenseMapper.toDto(expense)).thenReturn(new ExpenseResponseDto(id, description, category, null, null, null));

    ExpenseResponseDto result = expenseService.getById(id);

    assertEquals(id, result.id());
    assertEquals(description, result.description());
    assertEquals(category, result.category());
  }

  @Test
  void getById_ShouldThrowExpenseNotFoundException() {
    Long id = 1L;
    when(expenseRepository.findByUser_UsernameAndId("testuser", id)).thenReturn(Optional.empty());

    assertThrows(ExpenseNotFoundException.class, () -> expenseService.getById(id));
  }

  @Test
  void save_ShouldReturnExpense() {
    ExpenseRequestDto request = new ExpenseRequestDto("desc", Category.OTHERS, null);
    UserEntity user = UserEntity.builder().build();
    Expense expense = Expense.builder().build();
    String category = "OTHERS";

    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(expenseMapper.toEntity(request, user)).thenReturn(expense);
    when(expenseRepository.save(expense)).thenReturn(expense);
    when(expenseMapper.toDto(expense)).thenReturn(new ExpenseResponseDto(1L, "desc", category, null, null, null));

    ExpenseResponseDto result = expenseService.save(request);

    assertEquals(1L, result.id());
    assertEquals("desc", result.description());
    assertEquals(category, result.category());
  }

  @Test
  void save_ShouldThrowUsernameNotFoundException() {
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class, () -> expenseService.save(new ExpenseRequestDto("d", Category.OTHERS, null)));
  }

  @Test
  void update_ShouldUpdateExpense() {
    Long id = 1L;
    ExpenseRequestDto request = new ExpenseRequestDto("desc", Category.OTHERS, null);
    Expense expense = Expense.builder().build();
    String category = "OTHERS";

    when(expenseRepository.findByUser_UsernameAndId("testuser", id)).thenReturn(Optional.of(expense));
    when(expenseMapper.toDto(expense)).thenReturn(new ExpenseResponseDto(id, "desc", category, null, null, null));

    ExpenseResponseDto result = expenseService.update(id, request);

    assertEquals(id, result.id());
    assertEquals("desc", result.description());
    assertEquals(category, result.category());
  }

  @Test
  void update_ShouldThrowExpenseNotFoundException() {
    when(expenseRepository.findByUser_UsernameAndId("testuser", 1L)).thenReturn(Optional.empty());

    assertThrows(ExpenseNotFoundException.class, () -> expenseService.update(1L, new ExpenseRequestDto("d", Category.OTHERS, null)));
  }

  @Test
  void getAll_ShouldReturnPage() {
    ExpenseFilterDto filter = new ExpenseFilterDto(null, null, null, null);
    Pageable pageable = PageRequest.of(0, 10);
    Expense expense = Expense.builder().build();
    ExpenseResponseDto response = new ExpenseResponseDto(1L, "desc", "OTHERS", null, null, null);

    when(expenseRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(Collections.singletonList(expense)));
    when(expenseMapper.toDto(expense)).thenReturn(response);

    Page<ExpenseResponseDto> result = expenseService.getAll(filter, pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals(response, result.getContent().get(0));
  }

  @Test
  void delete_ShouldDeleteExpense() {
    Long id = 1L;
    Expense expense = Expense.builder().build();
    when(expenseRepository.findByUser_UsernameAndId("testuser", id)).thenReturn(Optional.of(expense));

    expenseService.delete(id);

    verify(expenseRepository).delete(expense);
  }

  @Test
  void delete_GivenInvalidId_ShouldDeleteExpense() {
    Long id = -1L;
    when(expenseRepository.findByUser_UsernameAndId("testuser", id)).thenReturn(Optional.empty());

    assertThrows(ExpenseNotFoundException.class, () -> expenseService.delete(id));

    verify(expenseRepository).findByUser_UsernameAndId("testuser", id);
    verify(expenseRepository, never()).delete(any(Expense.class));
  }
}
