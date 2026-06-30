package org.duckdns.massemiso.personal_blog.article;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

  @Mock
  ArticleRepository repository;
  @Mock
  ArticleMapper mapper;

  @Test
  void getAll_GivenTwoArticlesInPersistence_ShouldReturnListOfResponseDto() {
    // Arrange
    ArticleService articleService = new ArticleService(repository, mapper);
    LocalDate now = LocalDate.now();
    String nowStr = now.format(DateTimeFormatter.ofPattern("MMM d, YYYY"));
    List<Article> articles = List.of(new Article("t1", "c1", now),
        new Article("t2", "c2", now));
    articles.getFirst().setId(1L);
    articles.getLast().setId(2L);
    List<ArticleResponseDto> articleResponseDtos = List.of(
        new ArticleResponseDto(1L, "t1", "c1", nowStr),
        new ArticleResponseDto(2L, "t2", "c2", nowStr));

    // Mock
    when(repository.getAll())
        .thenReturn(articles);
    when(mapper.toDto(articles.getFirst()))
        .thenReturn(articleResponseDtos.getFirst());
    when(mapper.toDto(articles.getLast()))
        .thenReturn(articleResponseDtos.getLast());

    // Act
    List<ArticleResponseDto> actual = articleService.getAll();

    // Asserts
    assertThat(actual.size(), is(2));
    assertThat(actual.getFirst().id(), is(articles.getFirst().getId()));
    assertThat(actual.getLast().id(), is(articles.getLast().getId()));

    verify(repository).getAll();
    verify(mapper).toDto(articles.getFirst());
    verify(mapper).toDto(articles.getLast());
  }

  @Test
  void getAll_GivenNoneEntInPersistence_ShouldReturnAListEmptyOfResponseDto() {
    // Arrange
    ArticleService articleService = new ArticleService(repository, mapper);
    List<Article> articles = List.of();
    List<ArticleResponseDto> articleResponseDtos = List.of();

    // Mock
    when(repository.getAll())
        .thenReturn(List.of());

    // Act
    List<ArticleResponseDto> actual = articleService.getAll();

    // Asserts
    assertThat(actual.size(), is(0));

    verify(repository).getAll();
    verify(mapper, never()).toDto(any(Article.class));
  }

  @Test
  void getById_GivenValidId_ShouldReturnResponseDto() {
    // Arrange
    ArticleService articleService = new ArticleService(repository, mapper);
    Long validId = 1L;
    Article article = new Article("t1", "c1", null);
    article.setId(validId);
    ArticleResponseDto responseDto = new ArticleResponseDto(validId, "t1", "c1", null);

    // mock
    when(repository.getById(validId))
        .thenReturn(Optional.of(article));
    when(mapper.toDto(article))
        .thenReturn(responseDto);

    // Act
    ArticleResponseDto actual = articleService.getById(validId);

    // Assert
    assertThat(actual.id(), is(responseDto.id()));
    assertThat(actual.title(), is(responseDto.title()));
    assertThat(actual.content(), is(responseDto.content()));
    assertThat(actual.dop(), is(responseDto.dop()));

    verify(repository).getById(validId);
    verify(mapper).toDto(article);
  }

  @Test
  void getById_GivenInvalidId_ShouldThrowNoSuchElementException() {
    // Arrange
    ArticleService articleService = new ArticleService(repository, mapper);
    Long invalidId = -1L;

    // mock
    when(repository.getById(invalidId))
        .thenReturn(Optional.empty());

    // Act
    assertThrows(NoSuchElementException.class, () -> articleService.getById(invalidId));

    // Assert
    verify(repository).getById(invalidId);
    verify(mapper, never()).toDto(any(Article.class));
  }

  @Test
  void create_GivenValidRequest_ShouldReturnResponseDto() {
    // Arrange
    ArticleService articleService = new ArticleService(repository, mapper);
    ArticleRequestDto requestDto = new ArticleRequestDto("t1", "c1", LocalDate.now());
    Article article = new Article("t1", "c1", LocalDate.now());
    article.setId(1L);
    ArticleResponseDto responseDto = new ArticleResponseDto(1L, "t1", "c1", "...");

    // Mock
    when(mapper.toEntity(requestDto)).thenReturn(article);
    when(repository.save(article)).thenReturn(article);
    when(mapper.toDto(article)).thenReturn(responseDto);

    // Act
    ArticleResponseDto actual = articleService.create(requestDto);

    // Assert
    assertThat(actual, is(responseDto));
    verify(mapper).toEntity(requestDto);
    verify(repository).save(article);
    verify(mapper).toDto(article);
  }

  @Test
  void update_GivenValidIdAndRequest_ShouldReturnUpdatedResponseDto() {
    // Arrange
    ArticleService articleService = new ArticleService(repository, mapper);
    Long id = 1L;
    ArticleRequestDto requestDto = new ArticleRequestDto("new t", "new c", LocalDate.now());
    Article existingArticle = new Article("old t", "old c", LocalDate.now());
    existingArticle.setId(id);
    ArticleResponseDto responseDto = new ArticleResponseDto(id, "new t", "new c", "...");

    // Mock
    when(repository.getById(id)).thenReturn(Optional.of(existingArticle));
    when(repository.update(any(Article.class))).thenReturn(existingArticle);
    when(mapper.toDto(existingArticle)).thenReturn(responseDto);

    // Act
    ArticleResponseDto actual = articleService.update(id, requestDto);

    // Assert
    assertThat(actual, is(responseDto));
    verify(repository).getById(id);
    verify(repository).update(existingArticle);
    verify(mapper).toDto(existingArticle);
  }

  @Test
  void delete_GivenValidId_ShouldCallRepositoryDelete() {
    // Arrange
    ArticleService articleService = new ArticleService(repository, mapper);
    Long id = 1L;

    // Act
    articleService.delete(id);

    // Assert
    verify(repository).delete(id);
  }
}