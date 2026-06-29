package org.duckdns.massemiso.personal_blog.article;

public record ArticleResponseDto(Long id, String title, String content, String dop) {
  @Override
  public String toString() {
    return String.format(
        "ArticleResponseDto[id=%d, title=%s, content=%s, dop=%s]",
        id, title, content.length() >= 20 ? content.substring(0, 20) : content, dop);
  }
}
