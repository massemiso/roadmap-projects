package org.duckdns.massemiso.blogging_platform_api.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.test.util.ReflectionTestUtils;

class PostTest {

  @Test
  void update_GivenValidArgs_ShouldUpdateArgs() {
    // arrange
    Post post = Post.builder()
        .title("old title")
        .content("old content")
        .category("old category")
        .tags(List.of(new Tag("old tag")))
        .build();
    ReflectionTestUtils.setField(post, "id", 1L);
    String newTitle = "new title";
    String newContent = "new content";
    String newCategory = "new category";
    List<Tag> newTags = List.of(new Tag("new tag"));

    // act
    post.update(newTitle, newContent, newCategory, newTags);

    // assert
    assertNotNull(post);
    assertEquals(1L, post.getId());
    assertEquals(newTitle, post.getTitle());
    assertEquals(newContent, post.getContent());
    assertEquals(newCategory, post.getCategory());
    assertEquals(newTags.getFirst().getName(), post.getTags().getFirst().getName());
  }

  @Test
  void update_GivenBlankTitleAndOtherValidArgs_ShouldIgnoreTitleAndUpdateArgs() {
    // arrange
    Post post = Post.builder()
        .title("old title")
        .content("old content")
        .category("old category")
        .tags(List.of(new Tag("old tag")))
        .build();
    ReflectionTestUtils.setField(post, "id", 1L);
    String newTitle = "";
    String newContent = "new content";
    String newCategory = "new category";
    List<Tag> newTags = List.of(new Tag("new tag"));

    // act
    post.update(newTitle, newContent, newCategory, newTags);

    // assert
    assertNotNull(post);
    assertEquals(1L, post.getId());
    assertEquals("old title", post.getTitle());
    assertEquals(newContent, post.getContent());
    assertEquals(newCategory, post.getCategory());
    assertEquals(newTags.getFirst().getName(), post.getTags().getFirst().getName());
  }

  @Test
  void update_GivenBlankContentAndOtherValidArgs_ShouldIgnoreContentAndUpdateArgs() {
    // arrange
    Post post = Post.builder()
        .title("old title")
        .content("old content")
        .category("old category")
        .tags(List.of(new Tag("old tag")))
        .build();
    ReflectionTestUtils.setField(post, "id", 1L);
    String newTitle = "new title";
    String newContent = "";
    String newCategory = "new category";
    List<Tag> newTags = List.of(new Tag("new tag"));

    // act
    post.update(newTitle, newContent, newCategory, newTags);

    // assert
    assertNotNull(post);
    assertEquals(1L, post.getId());
    assertEquals(newTitle, post.getTitle());
    assertEquals("old content", post.getContent());
    assertEquals(newCategory, post.getCategory());
    assertEquals(newTags.getFirst().getName(), post.getTags().getFirst().getName());
  }

  @Test
  void update_GivenBlankCategoryAndOtherValidArgs_ShouldIgnoreCategoryAndUpdateArgs() {
    // arrange
    Post post = Post.builder()
        .title("old title")
        .content("old content")
        .category("old category")
        .tags(List.of(new Tag("old tag")))
        .build();
    ReflectionTestUtils.setField(post, "id", 1L);
    String newTitle = "new title";
    String newContent = "new content";
    String newCategory = "";
    List<Tag> newTags = List.of(new Tag("new tag"));

    // act
    post.update(newTitle, newContent, newCategory, newTags);

    // assert
    assertNotNull(post);
    assertEquals(1L, post.getId());
    assertEquals(newTitle, post.getTitle());
    assertEquals(newContent, post.getContent());
    assertEquals("old category", post.getCategory());
    assertEquals(newTags.getFirst().getName(), post.getTags().getFirst().getName());
  }

  @Test
  void update_GivenEmptyTagsAndOtherValidArgs_ShouldIgnoreTagsAndUpdateArgs() {
    // arrange
    Post post = Post.builder()
        .title("old title")
        .content("old content")
        .category("old category")
        .tags(List.of(new Tag("old tag")))
        .build();
    ReflectionTestUtils.setField(post, "id", 1L);
    String newTitle = "new title";
    String newContent = "new content";
    String newCategory = "new category";
    List<Tag> newTags = List.of();

    // act
    post.update(newTitle, newContent, newCategory, newTags);

    // assert
    assertNotNull(post);
    assertEquals(1L, post.getId());
    assertEquals(newTitle, post.getTitle());
    assertEquals(newContent, post.getContent());
    assertEquals(newCategory, post.getCategory());
    assertEquals("old tag", post.getTags().getFirst().getName());
  }

}