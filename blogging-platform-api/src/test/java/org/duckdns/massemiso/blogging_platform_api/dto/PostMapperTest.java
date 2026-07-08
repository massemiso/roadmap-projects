package org.duckdns.massemiso.blogging_platform_api.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import org.duckdns.massemiso.blogging_platform_api.persistence.Post;
import org.duckdns.massemiso.blogging_platform_api.persistence.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class PostMapperTest {

  @Test
  void toDto_GivenEntity_ShouldReturnResponseDto() {
    // arrange
    PostMapper postMapper = new PostMapper();
    Post entity = Post.builder()
        .title("some title")
        .content("some content")
        .category("some category")
        .tags(List.of(new Tag("some tag")))
        .build();
    ReflectionTestUtils.setField(entity, "id", 1L);
    ReflectionTestUtils.setField(entity, "createdAt", Instant.now());
    ReflectionTestUtils.setField(entity, "updatedAt", Instant.now());

    // act
    PostResponseDto responseDto = postMapper.toDto(entity);

    // assert
    assertNotNull(responseDto);
    assertEquals(entity.getId(), responseDto.id());
    assertEquals(entity.getTitle(), responseDto.title());
    assertEquals(entity.getContent(), responseDto.content());
    assertEquals(entity.getCategory(), responseDto.category());
    assertEquals(entity.getTags().getFirst().getName(), responseDto.tags().getFirst());
    assertEquals(entity.getCreatedAt().toString(), responseDto.createdAt());
    assertEquals(entity.getUpdatedAt().toString(), responseDto.updatedAt());
  }

  @Test
  void toEntity_GivenRequestDtoAndTags_ShouldReturnEntity() {
    // arrange
    PostMapper postMapper = new PostMapper();
    PostRequestDto requestDto = new PostRequestDto(
        "some title",
        "some content",
        "some category",
        List.of("some tag")
    );
    List<Tag> tags = List.of(new Tag("some tag"));

    // act
    Post entity = postMapper.toEntity(requestDto, tags);

    // assert
    assertNotNull(entity);
    assertNull(entity.getId());
    assertEquals(requestDto.title(), entity.getTitle());
    assertEquals(requestDto.content(), entity.getContent());
    assertEquals(requestDto.category(), entity.getCategory());
    assertEquals(tags.getFirst().getName(), entity.getTags().getFirst().getName());
    assertNull(entity.getCreatedAt());
    assertNull(entity.getUpdatedAt());
  }
}