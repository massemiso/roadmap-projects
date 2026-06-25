package org.duckdns.massemiso.personal_blog.article;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class Article {
  @Setter private Long id;
  private String title;
  private String content;
  private LocalDateTime dateOfPublication;

  @Builder
  public Article(String title, String content, LocalDateTime dateOfPublication) {
    this.title = title;
    this.content = content;
    this.dateOfPublication = dateOfPublication;
  }
}
