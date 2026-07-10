package org.duckdns.massemiso.todo_list_api.repository;

import org.duckdns.massemiso.todo_list_api.entity.Todo;
import org.springframework.data.jpa.domain.Specification;

public class TodoSpecifications {

  public static Specification<Todo> titleLike(String title) {
    return (root, query, criteriaBuilder) -> {
      if (title == null || title.trim().isEmpty()) {
        return null; // Ignore filter if empty
      }

      // Brings both the DB field and search term to lowercase for a case-insensitive search
      return criteriaBuilder.like(
          criteriaBuilder.lower(root.get("title")),
          "%" + title.toLowerCase() + "%"
      );
    };
  }

  public static Specification<Todo> descriptionLike(String description) {
    return (root, query, criteriaBuilder) -> {
      if (description == null || description.trim().isEmpty()) {
        return null; // Ignore filter if empty
      }

      // Brings both the DB field and search term to lowercase for a case-insensitive search
      return criteriaBuilder.like(
          criteriaBuilder.lower(root.get("description")),
          "%" + description.toLowerCase() + "%"
      );
    };
  }

  public static Specification<Todo> hasCompleted(Boolean completed) {
    return (root, query, criteriaBuilder) ->
        completed == null ? null : criteriaBuilder.equal(root.get("completed"), completed);
  }

}
