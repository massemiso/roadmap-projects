package org.duckdns.massemiso.personal_blog.article;

import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public class ArticleSpecifications {

  public static Specification<Article> titleLike(String title) {
    return (root, query, criteriaBuilder) -> {
      if (title == null || title.trim().isEmpty()) {
        return null; // Ignore filter if empty
      }

      // Brings both the DB field and search term to lowercase for a case-insensitive search
      return criteriaBuilder.like(
          criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    };
  }

  public static Specification<Article> dateOfPublicationLike(String date) {
    return (root, query, criteriaBuilder) -> {
      if (date == null || date.trim().isEmpty()) {
        return null; // Ignore filter if empty
      }

      try {
        LocalDate localDate = LocalDate.parse(date);
        return criteriaBuilder.equal(root.get("dateOfPublication"), localDate);
      } catch (Exception e) {
        return null; // Ignore if invalid date format
      }
    };
  }
}
