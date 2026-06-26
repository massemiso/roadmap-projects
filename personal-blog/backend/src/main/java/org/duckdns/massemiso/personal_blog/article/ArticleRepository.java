package org.duckdns.massemiso.personal_blog.article;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository {
  List<Article> getAll();

  Optional<Article> getById(Long id);

  Article save(Article article);
}
