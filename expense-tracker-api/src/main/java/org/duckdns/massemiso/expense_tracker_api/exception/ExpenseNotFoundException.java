package org.duckdns.massemiso.expense_tracker_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ExpenseNotFoundException extends RuntimeException {

  public ExpenseNotFoundException(Long id) {
    super(String.format("Expense not found with id %d", id));
  }
}
