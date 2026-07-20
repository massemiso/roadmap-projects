package org.duckdns.massemiso.expense_tracker_api.repository;

import java.util.Optional;
import org.duckdns.massemiso.expense_tracker_api.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>,
    JpaSpecificationExecutor<Expense> {
  Optional<Expense> findByUser_UsernameAndId(String username, Long id);
}
