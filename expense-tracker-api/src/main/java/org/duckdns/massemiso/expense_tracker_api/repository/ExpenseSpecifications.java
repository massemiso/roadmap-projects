package org.duckdns.massemiso.expense_tracker_api.repository;

import java.time.LocalDate;
import org.duckdns.massemiso.expense_tracker_api.model.Category;
import org.duckdns.massemiso.expense_tracker_api.model.Expense;
import org.springframework.data.jpa.domain.Specification;

public class ExpenseSpecifications {

  public static Specification<Expense> dateAsPastWeek() {
    return (root, query, criteriaBuilder) -> {
      LocalDate oneWeekAgo = LocalDate.now().minusDays(7);
      return criteriaBuilder.greaterThanOrEqualTo(root.get("date"), oneWeekAgo);
    };
  }

  public static Specification<Expense> dateAsPastMonth() {
    return (root, query, criteriaBuilder) -> {
      LocalDate oneMonthAgo = LocalDate.now().minusDays(31);
      return criteriaBuilder.greaterThanOrEqualTo(root.get("date"), oneMonthAgo);
    };
  }

  public static Specification<Expense> dateAsLastThreeMonths() {
    return (root, query, criteriaBuilder) -> {
      LocalDate threeMonthsAgo = LocalDate.now().minusDays(31 * 3);
      return criteriaBuilder.greaterThanOrEqualTo(root.get("date"), threeMonthsAgo);
    };
  }

  public static Specification<Expense> dateBetweenTwoDates(LocalDate firstDate, LocalDate secondDate) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.between(root.get("date"), firstDate, secondDate);
  }

  public static Specification<Expense> ownedBy(String username) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("user").get("username"), username);
  }

  public static Specification<Expense> categoryLike(Category category) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("category"), category);
  }
}
