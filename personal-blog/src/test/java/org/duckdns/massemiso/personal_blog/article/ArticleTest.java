package org.duckdns.massemiso.personal_blog.article;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ArticleTest {

  @Test
  void builder() {
    // arrange
    String title = "Some title";
    String content = "Some content";
    LocalDate dateOfPublication = LocalDate.now();
    Article expected = new Article(title, content, dateOfPublication);

    // act
    Article real = Article.builder().title(title).content(content)
        .dateOfPublication(dateOfPublication).build();

    // assert
    assertEquals(expected.getTitle(), real.getTitle(), "Titles are not equal");
    assertEquals(expected.getContent(), real.getContent(), "Content are not equal");
    assertEquals(expected.getDateOfPublication(), real.getDateOfPublication(),
        "Date of Publication are not equal");
    assertNull(expected.getId(), "Id should be null");
  }


  @Test
  void update_GivenAllValidArgs_ShouldUpdateFields() {
    // arrange
    Long id = 1L;
    Article entity = new Article("some title", "some content", LocalDate.now());
    entity.setId(id);

    // act
    String newTitle = "new title";
    String newContent = "new content";
    LocalDate newDop = LocalDate.now().withDayOfMonth(1);
    entity.update(newTitle, newContent, newDop);

    // assert
    assertEquals(newTitle, entity.getTitle(), "Titles are not equal");
    assertEquals(newContent, entity.getContent(), "Content are not equal");
    assertEquals(newDop, entity.getDateOfPublication(), "Date of Publication are not equal");
    assertEquals(id, entity.getId(), "Id are not equal");
  }


  @Test
  void update_GivenInvalidTitle_ShouldUpdateFieldsExceptTitle() {
    // arrange
    Long id = 1L;
    String oldTitle = "some old title";
    Article entity = new Article(oldTitle, "some content", LocalDate.now());
    entity.setId(id);

    // act
    String newTitle = "";
    String newContent = "new content";
    LocalDate newDop = LocalDate.now().withDayOfMonth(1);
    entity.update(newTitle, newContent, newDop);

    // assert
    assertEquals(oldTitle, entity.getTitle(), "Titles are not equal");
    assertEquals(newContent, entity.getContent(), "Content are not equal");
    assertEquals(newDop, entity.getDateOfPublication(), "Date of Publication are not equal");
    assertEquals(id, entity.getId(), "Id are not equal");
  }

  @Test
  void update_GivenInvalidContent_ShouldUpdateFieldsExceptContent() {
    // arrange
    Long id = 1L;
    String oldContent = "some old content";
    Article entity = new Article("some title", oldContent, LocalDate.now());
    entity.setId(id);

    // act
    String newTitle = "new title";
    String newContent = "";
    LocalDate newDop = LocalDate.now().withDayOfMonth(1);
    entity.update(newTitle, newContent, newDop);

    // assert
    assertEquals(newTitle, entity.getTitle(), "Titles are not equal");
    assertEquals(oldContent, entity.getContent(), "Content are not equal");
    assertEquals(newDop, entity.getDateOfPublication(), "Date of Publication are not equal");
    assertEquals(id, entity.getId(), "Id are not equal");
  }
}