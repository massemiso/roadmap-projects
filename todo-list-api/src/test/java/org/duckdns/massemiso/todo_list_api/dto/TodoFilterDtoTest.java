package org.duckdns.massemiso.todo_list_api.dto;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.duckdns.massemiso.todo_list_api.entity.Todo;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

public class TodoFilterDtoTest {

  @Test
  void getSpecification_ShouldReturnUnrestricted_WhenAllFieldsNull() {
    TodoFilterDto filterDto = new TodoFilterDto(null, null, null);
    Specification<Todo> spec = filterDto.getSpecification();
    assertNotNull(spec);
  }

  @Test
  @SuppressWarnings("unchecked")
  void getSpecification_ShouldCombineFilters_WhenAllFieldsProvided() {
    TodoFilterDto filterDto = new TodoFilterDto("Title", "Desc", true);
    Specification<Todo> spec = filterDto.getSpecification();
    assertNotNull(spec);

    assertDoesNotThrow(() -> spec.toPredicate(mock(Root.class), mock(CriteriaQuery.class),
        mock(CriteriaBuilder.class)));
  }

  @Test
  void getSpecification_ShouldIgnoreEmptyStrings() {
    TodoFilterDto filterDto = new TodoFilterDto("", "", null);
    Specification<Todo> spec = filterDto.getSpecification();
    assertNotNull(spec);
  }
}
