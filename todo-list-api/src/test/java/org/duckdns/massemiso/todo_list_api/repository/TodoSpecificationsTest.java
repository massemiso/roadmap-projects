package org.duckdns.massemiso.todo_list_api.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.duckdns.massemiso.todo_list_api.entity.Todo;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

public class TodoSpecificationsTest {

  @SuppressWarnings("unchecked")
  @Test
  void titleLike_ShouldReturnNullPredicate_WhenTitleIsEmpty() {
    Specification<Todo> spec = TodoSpecifications.titleLike("");
    assertNotNull(spec);
    assertNull(
        spec.toPredicate(mock(Root.class), mock(CriteriaQuery.class), mock(CriteriaBuilder.class)));
  }

  @SuppressWarnings("unchecked")
  @Test
  void descriptionLike_ShouldReturnNullPredicate_WhenDescriptionIsEmpty() {
    Specification<Todo> spec = TodoSpecifications.descriptionLike("");
    assertNotNull(spec);
    assertNull(
        spec.toPredicate(mock(Root.class), mock(CriteriaQuery.class), mock(CriteriaBuilder.class)));
  }

  @SuppressWarnings("unchecked")
  @Test
  void hasCompleted_ShouldReturnNullPredicate_WhenCompletedIsNull() {
    Specification<Todo> spec = TodoSpecifications.hasCompleted(null);
    assertNotNull(spec);
    assertNull(
        spec.toPredicate(mock(Root.class), mock(CriteriaQuery.class), mock(CriteriaBuilder.class)));
  }
}
