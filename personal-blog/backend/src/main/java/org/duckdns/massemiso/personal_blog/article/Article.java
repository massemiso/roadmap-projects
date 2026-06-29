package org.duckdns.massemiso.personal_blog.article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Article {
  private Long id;
  private String title;
  private String content;
  private LocalDate dateOfPublication;

  @Builder
  public Article(String title, String content, LocalDate dateOfPublication) {
    this.title = title;
    this.content = content;
    this.dateOfPublication = dateOfPublication;
  }

  public void update(String title, String content, LocalDate dateOfPublication) {
    if (!title.isBlank()){
      this.title = title;
    }
    if (!content.isBlank()){
      this.content = content;
    }
    this.dateOfPublication = dateOfPublication;
  }
}
