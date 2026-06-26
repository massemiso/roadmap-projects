package org.duckdns.massemiso.personal_blog.article;

import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class ArticleMapper {
  public ArticleResponseDto toDto(Article entity) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, YYYY");
    return new ArticleResponseDto(
        entity.getId(),
        entity.getTitle(),
        entity.getContent(),
        entity.getDateOfPublication().format(formatter));
  }

  public Article toEntity(ArticleRequestDto dto) {
    return Article.builder()
        .title(dto.title())
        .content(dto.content())
        .dateOfPublication(dto.dateOfPublication())
        .build();
  }
}
