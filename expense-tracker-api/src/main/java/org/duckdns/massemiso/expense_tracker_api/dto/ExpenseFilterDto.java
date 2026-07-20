package org.duckdns.massemiso.expense_tracker_api.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import org.duckdns.massemiso.expense_tracker_api.model.Category;
import org.duckdns.massemiso.expense_tracker_api.model.Expense;
import org.duckdns.massemiso.expense_tracker_api.repository.ExpenseSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

public record ExpenseFilterDto(
    @Enumerated(EnumType.STRING)
    ExpenseFilter filter,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate firstDate,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate secondDate,
    @Enumerated(EnumType.STRING)
    Category category
) {
  public Specification<Expense> getSpecification() {
    Specification<Expense> spec = Specification.unrestricted();

    if (filter == null) {
      return spec;
    }

    switch (filter) {
      case PAST_WEEK -> {
        return spec.and(ExpenseSpecifications.dateAsPastWeek());
      }
      case PAST_MONTH -> {
        return spec.and(ExpenseSpecifications.dateAsPastMonth());
      }
      case LAST_THREE_MONTHS -> {
        return spec.and(ExpenseSpecifications.dateAsLastThreeMonths());
      }
      case CUSTOM -> {
        if (firstDate != null && secondDate != null) {
          return spec.and(ExpenseSpecifications.dateBetweenTwoDates(firstDate, secondDate));
        }
      }
      case CATEGORY -> {
        if (category != null) {
          return spec.and(ExpenseSpecifications.categoryLike(category));
        }
      }
    }
    return spec;
  }
}
