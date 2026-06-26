package org.duckdns.massemiso.personal_blog.article;

import java.time.LocalDate;
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
  private LocalDate dateOfPublication;

  @Builder
  public Article(String title, String content, LocalDate dateOfPublication) {
    this.title = title;
    this.content = content;
    this.dateOfPublication = dateOfPublication;
  }
}
