package org.duckdns.massemiso.expense_tracker_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import org.duckdns.massemiso.expense_tracker_api.model.Category;
import org.duckdns.massemiso.expense_tracker_api.model.Expense;
import org.duckdns.massemiso.expense_tracker_api.repository.ExpenseSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

@Schema(description = "DTO for filtering expenses")
public record ExpenseFilterDto(
    @Schema(example = "PAST_WEEK", requiredMode = RequiredMode.NOT_REQUIRED)
    @Enumerated(EnumType.STRING)
    ExpenseFilter filter,

    @Schema(requiredMode = RequiredMode.NOT_REQUIRED)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate firstDate,

    @Schema(requiredMode = RequiredMode.NOT_REQUIRED)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate secondDate,

    @Schema(requiredMode = RequiredMode.NOT_REQUIRED)
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
