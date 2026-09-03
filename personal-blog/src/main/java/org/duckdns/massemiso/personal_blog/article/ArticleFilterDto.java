package org.duckdns.massemiso.personal_blog.article;

import org.springframework.data.jpa.domain.Specification;

public record ArticleFilterDto(String title, String dateOfPublication) {

  public Specification<Article> getSpecification() {
    Specification<Article> spec = Specification.unrestricted();
    if (title != null && !title.isEmpty()) {
      spec = spec.and(ArticleSpecifications.titleLike(title));
    }
    if (dateOfPublication != null && !dateOfPublication.isEmpty()) {
      spec = spec.and(ArticleSpecifications.dateOfPublicationLike(dateOfPublication));
    }
    return spec;
  }
}
