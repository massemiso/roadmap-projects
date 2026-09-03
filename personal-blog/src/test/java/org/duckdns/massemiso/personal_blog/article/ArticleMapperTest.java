package org.duckdns.massemiso.personal_blog.article;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

class ArticleMapperTest {

  @Test
  void toDto_GivenValidArticle_ShouldReturnDtoResponse() {
    // arrange
    ArticleMapper mapper = new ArticleMapper();

    Long id = 1L;
    String title = "some title";
    String content = "some content";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, YYYY");
    LocalDate dop = LocalDate.now();
    String dopStr = dop.format(formatter);

    Article article = new Article(title, content, dop);
    article.setId(id);

    // act
    ArticleResponseDto responseDto = mapper.toDto(article, content);

    // assert
    assertNotNull(responseDto, "Dto should not be null");
    assertEquals(article.getId(), responseDto.id(), "Ids should be equal");
    assertEquals(article.getTitle(), responseDto.title(), "Titles should be equal");
    assertEquals(article.getContent(), responseDto.content(),
        "Content should be equal and with HTML format");
    assertEquals(dopStr, responseDto.dop(), "Date of publications should be equal");
  }

  @Test
  void toEditDto_GivenValidArticle_ShouldReturnDtoResponse() {
    // arrange
    ArticleMapper mapper = new ArticleMapper();

    Long id = 1L;
    String title = "some title";
    String mdContent = "# some content";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, YYYY");
    LocalDate dop = LocalDate.now();
    String dopStr = dop.format(formatter);

    Article article = new Article(title, mdContent, dop);
    article.setId(id);

    // act
    ArticleResponseDto responseDto = mapper.toDto(article);

    // assert
    assertNotNull(responseDto, "Dto should not be null");
    assertEquals(article.getId(), responseDto.id(), "Ids should be equal");
    assertEquals(article.getTitle(), responseDto.title(), "Titles should be equal");
    assertEquals(
        article.getContent(), responseDto.content(), "Content should be equal and in md format");
    assertEquals(dopStr, responseDto.dop(), "Date of publications should be equal");
  }

  @Test
  void toEntity_GivenValidRequestDto_ShouldReturnEntity() {
    // arrange
    ArticleMapper mapper = new ArticleMapper();

    String title = "some title";
    String content = "some content";
    LocalDate dop = LocalDate.now();

    ArticleRequestDto requestDto = new ArticleRequestDto(title, content, dop);

    // act
    Article entity = mapper.toEntity(requestDto);

    // assert
    assertNotNull(entity, "Entity should not be null");
    assertNull(entity.getId(), "Id should be null");
    assertEquals(requestDto.title(), entity.getTitle(), "Titles should be equal");
    assertEquals(requestDto.content(), entity.getContent(), "Content should be equal");
    assertEquals(
        requestDto.dateOfPublication(),
        entity.getDateOfPublication(),
        "Date of publications should be equal");
  }
}
