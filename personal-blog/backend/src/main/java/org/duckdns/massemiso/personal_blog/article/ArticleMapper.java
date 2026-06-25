package org.duckdns.massemiso.personal_blog.article;

import org.springframework.stereotype.Component;

@Component
public class ArticleMapper {
  public ArticleResponseDto toDto(Article entity) {
    return new ArticleResponseDto(
        entity.getId(), entity.getTitle(), entity.getContent(), entity.getDateOfPublication());
  }

  public Article toEntity(ArticleRequestDto dto) {
    return Article.builder()
        .title(dto.title())
        .content(dto.content())
        .dateOfPublication(dto.dateOfPublication())
        .build();
  }
}
