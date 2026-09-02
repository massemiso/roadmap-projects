package org.duckdns.massemiso.personal_blog.article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

public record ArticleRequestDto(
    @NotBlank(message = "Title cannot be blank") String title,
    @NotBlank(message = "Content cannot be blank") String content,
    @NotNull(message = "Publication date cannot be empty")
        @PastOrPresent(message = "Publication date cannot be in the future")
        LocalDate dateOfPublication) {

  @Override
  public String toString() {
    return "ArticleRequestDto["
        + title
        + ", content="
        + ((content.length() < 12) ? content : content.substring(0, 12))
        + ", dateOfPublication="
        + dateOfPublication
        + "]";
  }
}
