package org.duckdns.massemiso.expense_tracker_api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.duckdns.massemiso.expense_tracker_api.model.Category;
import org.duckdns.massemiso.expense_tracker_api.model.Expense;
import org.duckdns.massemiso.expense_tracker_api.model.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ExpenseMapperTest {

  private final ExpenseMapper expenseMapper = new ExpenseMapper();

  @Test
  void toEntity_ShouldMapCorrectly() {
    ExpenseRequestDto requestDto = new ExpenseRequestDto(
        "Description",
        Category.ELECTRONICS,
        LocalDate.now()
    );
    UserEntity user = UserEntity.builder()
        .username("user")
        .password("pass")
        .build();

    Expense expense = expenseMapper.toEntity(requestDto, user);

    assertEquals(requestDto.description(), expense.getDescription());
    assertEquals(requestDto.category(), expense.getCategory());
    assertEquals(requestDto.date(), expense.getDate());
    assertEquals(user, expense.getUser());
  }

  @Test
  void toDto_ShouldMapCorrectly() {
    UserEntity user = UserEntity.builder()
        .username("user")
        .password("pass")
        .build();
    Expense expense = Expense.builder()
        .description("Description")
        .category(Category.ELECTRONICS)
        .date(LocalDate.now())
        .user(user)
        .build();
    ReflectionTestUtils.setField(expense, "id", 1L);

    ExpenseResponseDto responseDto = expenseMapper.toDto(expense);

    assertEquals(1L, responseDto.id());
    assertEquals(expense.getDescription(), responseDto.description());
    assertEquals(expense.getCategory().toString(), responseDto.category());
    assertEquals(expense.getDate(), responseDto.date());
  }
}
