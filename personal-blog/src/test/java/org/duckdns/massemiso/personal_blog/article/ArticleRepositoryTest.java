package org.duckdns.massemiso.personal_blog.article;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ArticleRepositoryTest {

  @Autowired
  private ArticleRepository repository;

  @Test
  void save_GivenArticle_ShouldPersistArticle() {
    // Arrange
    Article article = new Article("Test Title", "Test Content", LocalDate.now());

    // Act
    Article saved = repository.save(article);

    // Assert
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getTitle()).isEqualTo("Test Title");
  }

  @Test
  void findAll_ShouldReturnAListOfArticles() {
    // Arrange
    Article article = repository.save(new Article("Test Title", "Test Content", LocalDate.now()));
    Article article2 = repository.save(
        new Article("Test Title 2", "Test Content 2", LocalDate.now()));

    // Act
    List<Article> actual = repository.findAll();

    // Assert
    assertEquals(2, actual.size());
    assertEquals(article, actual.getFirst());
    assertEquals(article2, actual.getLast());
  }

  @Test
  void findAll_WhenNoArticlesInDb_ShouldReturnAnEmptyList() {
    // Act
    List<Article> actual = repository.findAll();

    // Assert
    assertEquals(0, actual.size());
  }

  @Test
  void findById_GivenId_ShouldReturnOptionalOfArticle() {
    // Arrange
    Article article = repository.save(new Article("Test Title", "Test Content", LocalDate.now()));

    // Act
    Optional<Article> actual = repository.findById(article.getId());

    // Assert
    assertTrue(actual.isPresent());
    assertThat(actual.get()).isEqualTo(article);
  }

  @Test
  void findById_GivenInvalidId_ShouldReturnEmptyOptional() {
    // Act
    Optional<Article> actual = repository.findById(1L);

    // Assert
    assertTrue(actual.isEmpty());
  }

  @Test
  void deleteById_GivenId_ShouldDeleteArticle() {
    // Arrange
    Article article = repository.save(new Article("Test Title", "Test Content", LocalDate.now()));

    // Act
    repository.deleteById(article.getId());

    // Assert
    assertTrue(repository.findById(article.getId()).isEmpty());
  }

}