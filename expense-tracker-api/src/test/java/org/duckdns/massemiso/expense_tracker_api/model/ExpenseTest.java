package org.duckdns.massemiso.expense_tracker_api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ExpenseTest {

  @Test
  void update_GivenNotBlankArgs_ShouldUpdateAllFields() {
    // arrange
    Expense expense = Expense.builder()
        .description("old description")
        .category(Category.OTHERS)
        .date(LocalDate.now())
        .user(UserEntity.builder().username("same user").build())
        .build();
    String newDescription = "new description";
    Category newCategory = Category.LEISURE;
    LocalDate newDate = LocalDate.now().minusDays(1);

    // act
    expense.update(newDescription, newCategory, newDate);

    // assert
    assertNotNull(expense);
    assertEquals(newDescription, expense.getDescription());
    assertEquals(newCategory, expense.getCategory());
    assertEquals(newDate, expense.getDate());
    assertEquals("same user", expense.getUser().getUsername());
  }

  @Test
  void update_GivenBlankOrNullArgs_ShouldLeaveExpenseAsItIs() {
    // arrange
    Expense expense = Expense.builder()
        .description("old description")
        .category(Category.OTHERS)
        .date(LocalDate.now())
        .user(UserEntity.builder().username("same user").build())
        .build();

    // act
    expense.update("", null, null);

    // assert
    assertNotNull(expense);
    assertEquals("old description", expense.getDescription());
    assertEquals(Category.OTHERS, expense.getCategory());
    assertEquals(LocalDate.now(), expense.getDate());
    assertEquals("same user", expense.getUser().getUsername());
  }
}