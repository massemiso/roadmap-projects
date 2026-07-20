package org.duckdns.massemiso.expense_tracker_api.dto;

import org.duckdns.massemiso.expense_tracker_api.model.Expense;
import org.duckdns.massemiso.expense_tracker_api.model.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {

  public Expense toEntity(ExpenseRequestDto requestDto, UserEntity user) {
    return Expense.builder()
        .description(requestDto.description())
        .category(requestDto.category())
        .date(requestDto.date())
        .user(user)
        .build();
  }

  public ExpenseResponseDto toDto(Expense expense) {
    return new ExpenseResponseDto(
        expense.getId(),
        expense.getDescription(),
        expense.getCategory().toString(),
        expense.getDate(),
        expense.getCreatedDate(),
        expense.getLastModifiedDate()
    );
  }
}
