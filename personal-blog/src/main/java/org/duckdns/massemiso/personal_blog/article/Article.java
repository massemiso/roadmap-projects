package org.duckdns.massemiso.personal_blog.article;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Entity
public class Article {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "text")
  private String content;

  @Column(nullable = false, name = "dop")
  private LocalDate dateOfPublication;

  @Builder
  public Article(String title, String content, LocalDate dateOfPublication) {
    this.title = title;
    this.content = content;
    this.dateOfPublication = dateOfPublication;
  }

  public void update(String title, String content, LocalDate dateOfPublication) {
    if (!title.isBlank()) {
      this.title = title;
    }
    if (!content.isBlank()) {
      this.content = content;
    }
    this.dateOfPublication = dateOfPublication;
  }
}
