package org.duckdns.massemiso.todo_list_api.dto;

import org.duckdns.massemiso.todo_list_api.entity.Todo;
import org.duckdns.massemiso.todo_list_api.repository.TodoSpecifications;
import org.springframework.data.jpa.domain.Specification;

public record TodoFilterDto(
    String title,
    String description,
    Boolean completed
) {

  public Specification<Todo> getSpecification() {
    Specification<Todo> spec = Specification.unrestricted();
    if (title != null && !title.isEmpty()) {
      spec = spec.and(TodoSpecifications.titleLike(title));
    }
    if (description != null && !description.isEmpty()) {
      spec = spec.and(TodoSpecifications.descriptionLike(description));
    }
    if (completed != null) {
      spec = spec.and(TodoSpecifications.hasCompleted(completed));
    }
    return spec;
  }
}
