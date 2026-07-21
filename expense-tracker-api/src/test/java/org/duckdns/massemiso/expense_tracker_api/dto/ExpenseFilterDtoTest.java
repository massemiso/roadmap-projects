package org.duckdns.massemiso.expense_tracker_api.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import org.duckdns.massemiso.expense_tracker_api.model.Category;
import org.duckdns.massemiso.expense_tracker_api.model.Expense;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

class ExpenseFilterDtoTest {

  @Test
  void getSpecification_WhenFilterIsNull_ShouldReturnUnrestrictedSpec() {
    ExpenseFilterDto filterDto = new ExpenseFilterDto(null, null, null, null);
    Specification<Expense> spec = filterDto.getSpecification();
    assertNotNull(spec);
  }

  @Test
  void getSpecification_WhenFilterIsPastWeek_ShouldReturnSpec() {
    ExpenseFilterDto filterDto = new ExpenseFilterDto(ExpenseFilter.PAST_WEEK, null, null, null);
    Specification<Expense> spec = filterDto.getSpecification();
    assertNotNull(spec);
  }

  @Test
  void getSpecification_WhenFilterIsPastMonth_ShouldReturnSpec() {
    ExpenseFilterDto filterDto = new ExpenseFilterDto(ExpenseFilter.PAST_MONTH, null, null, null);
    Specification<Expense> spec = filterDto.getSpecification();
    assertNotNull(spec);
  }

  @Test
  void getSpecification_WhenFilterIsLastThreeMonths_ShouldReturnSpec() {
    ExpenseFilterDto filterDto = new ExpenseFilterDto(ExpenseFilter.LAST_THREE_MONTHS, null, null, null);
    Specification<Expense> spec = filterDto.getSpecification();
    assertNotNull(spec);
  }

  @Test
  void getSpecification_WhenFilterIsCustomWithDates_ShouldReturnSpec() {
    ExpenseFilterDto filterDto = new ExpenseFilterDto(ExpenseFilter.CUSTOM, LocalDate.now().minusDays(10), LocalDate.now(), null);
    Specification<Expense> spec = filterDto.getSpecification();
    assertNotNull(spec);
  }

  @Test
  void getSpecification_WhenFilterIsCategory_ShouldReturnSpec() {
    ExpenseFilterDto filterDto = new ExpenseFilterDto(ExpenseFilter.CATEGORY, null, null, Category.ELECTRONICS);
    Specification<Expense> spec = filterDto.getSpecification();
    assertNotNull(spec);
  }
}
