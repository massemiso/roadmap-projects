package org.duckdns.massemiso.blogging_platform_api.service;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.duckdns.massemiso.blogging_platform_api.dto.PostMapper;
import org.duckdns.massemiso.blogging_platform_api.dto.PostRequestDto;
import org.duckdns.massemiso.blogging_platform_api.dto.PostResponseDto;
import org.duckdns.massemiso.blogging_platform_api.exception.PostNotFoundException;
import org.duckdns.massemiso.blogging_platform_api.persistence.Post;
import org.duckdns.massemiso.blogging_platform_api.persistence.Tag;
import org.duckdns.massemiso.blogging_platform_api.persistence.repository.PostRepository;
import org.duckdns.massemiso.blogging_platform_api.persistence.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
  @Mock private PostRepository postRepository;
  @Mock private PostMapper postMapper;
  @Mock private TagRepository tagRepository;
  @InjectMocks private PostService postService;

  @Test
  void getAll_GivenNoFilter_ShouldReturnAListOfResponseDto() {
    // arrange & mock
    List<Post> posts =
        List.of(
            Post.builder()
                .title("some title")
                .content("some content")
                .category("some category")
                .tags(List.of(new Tag("some tag")))
                .build());
    List<PostResponseDto> expected =
        List.of(
            new PostResponseDto(
                1L,
                "some title",
                "some content",
                "some category",
                List.of("some tag"),
                LocalDateTime.now().toString(),
                LocalDateTime.now().toString()));

    when(postRepository.findAll()).thenReturn(posts);
    when(postMapper.toDto(posts.getFirst())).thenReturn(expected.getFirst());

    // act
    List<PostResponseDto> actual = postService.getAll("", "");

    // assert
    assertThat(actual)
        .isNotNull()
        .hasSameSizeAs(expected)
        .hasOnlyElementsOfType(PostResponseDto.class)
        .isEqualTo(expected);

    verify(postRepository).findAll();
    verify(postMapper).toDto(posts.getFirst());
  }

  @Test
  void getAll_GivenTermFilter_ShouldReturnAListOfResponseDtoFilteredByTerm() {
    // arrange & mock
    String term = "other";
    List<Post> posts =
        List.of(
            Post.builder()
                .title("other title")
                .content("other content")
                .category("other category")
                .tags(List.of(new Tag("other tag")))
                .build());
    List<PostResponseDto> expected =
        List.of(
            new PostResponseDto(
                2L,
                "other title",
                "other content",
                "other category",
                List.of("other tag"),
                LocalDateTime.now().toString(),
                LocalDateTime.now().toString()));

    when(postRepository.findBySearchTerm(term)).thenReturn(posts);
    when(postMapper.toDto(posts.getFirst())).thenReturn(expected.getFirst());

    // act
    List<PostResponseDto> actual = postService.getAll(term, "");

    // assert
    assertThat(actual)
        .isNotNull()
        .hasSameSizeAs(expected)
        .hasOnlyElementsOfType(PostResponseDto.class)
        .isEqualTo(expected);

    verify(postRepository).findBySearchTerm(term);
    verify(postMapper).toDto(posts.getFirst());
  }

  @Test
  void getAll_GivenTagFilter_ShouldReturnAListOfResponseDtoFilteredByTag() {
    // arrange & mock
    String tag = "other";
    List<Post> posts =
        List.of(
            Post.builder()
                .title("other title")
                .content("other content")
                .category("other category")
                .tags(List.of(new Tag(tag)))
                .build());
    List<PostResponseDto> expected =
        List.of(
            new PostResponseDto(
                2L,
                "other title",
                "other content",
                "other category",
                List.of(tag),
                LocalDateTime.now().toString(),
                LocalDateTime.now().toString()));

    when(postRepository.findAll()).thenReturn(posts);
    when(postMapper.toDto(posts.getFirst())).thenReturn(expected.getFirst());

    // act
    List<PostResponseDto> actual = postService.getAll("", "other");

    // assert
    assertThat(actual)
        .isNotNull()
        .hasSameSizeAs(expected)
        .hasOnlyElementsOfType(PostResponseDto.class)
        .isEqualTo(expected);

    verify(postRepository).findAll();
    verify(postMapper).toDto(posts.getFirst());
  }

  @Test
  void getAll_GivenTermAndTagFilter_ShouldReturnAListOfResponseDtoFilteredByTermAndTag() {
    // arrange & mock
    String term = "other";
    String tag = "other";
    List<Post> posts =
        List.of(
            Post.builder()
                .title("other title")
                .content("other content")
                .category("other category")
                .tags(List.of(new Tag(tag)))
                .build());
    List<PostResponseDto> expected =
        List.of(
            new PostResponseDto(
                2L,
                "other title",
                "other content",
                "other category",
                List.of(tag),
                LocalDateTime.now().toString(),
                LocalDateTime.now().toString()));

    when(postRepository.findBySearchTerm(term)).thenReturn(posts);
    when(postMapper.toDto(posts.getFirst())).thenReturn(expected.getFirst());

    // act
    List<PostResponseDto> actual = postService.getAll(term, tag);

    // assert
    assertThat(actual)
        .isNotNull()
        .hasSameSizeAs(expected)
        .hasOnlyElementsOfType(PostResponseDto.class)
        .isEqualTo(expected);

    verify(postRepository).findBySearchTerm(term);
    verify(postMapper).toDto(posts.getFirst());
  }

  @Test
  void getById_GivenValidId_ShouldReturnResponseDto() {
    // arrange & mock
    Post post = Post.builder()
        .title("some title")
        .content("some content")
        .category("some category")
        .tags(List.of(new Tag("some tag")))
        .build();
    Long id = 1L;
    ReflectionTestUtils.setField(post, "id", id);
    PostResponseDto expected = new PostResponseDto(
        post.getId(),
        post.getTitle(),
        post.getContent(),
        post.getCategory(),
        List.of("some tag"),
        LocalDateTime.now().toString(),
        LocalDateTime.now().toString()
    );

    when(postRepository.findById(id)).thenReturn(Optional.of(post));
    when(postMapper.toDto(post)).thenReturn(expected);

    // act
    PostResponseDto actual = postService.getById(id);

    // assert
    assertNotNull(actual);
    assertEquals(post.getId(), actual.id());
    assertEquals(post.getTitle(), actual.title());
    assertEquals(post.getContent(), actual.content());
    assertEquals(post.getCategory(), actual.category());
    assertEquals(post.getTags().getFirst().getName(), actual.tags().getFirst());
    assertThat(actual.createdAt()).isEqualTo(expected.createdAt());
    assertThat(actual.updatedAt()).isEqualTo(expected.updatedAt());

    verify(postRepository).findById(id);
    verify(postMapper).toDto(post);
  }

  @Test
  void getById_GivenInvalidId_ShouldThrowPostNotFoundException() {
    // arrange & mock
    Long id = -1L;
    when(postRepository.findById(id)).thenReturn(Optional.empty());

    // act
    assertThrows(PostNotFoundException.class, () -> postService.getById(id));

    // assert
    verify(postRepository).findById(id);
    verify(postMapper, never()).toDto(any(Post.class));
  }

  @Test
  void create_GivenValidRequestDtoAndTagAlreadyInDb_ShouldReturnResponseDto() {
    // arrange & mock
    PostRequestDto requestDto = new PostRequestDto(
        "some title",
        "some content",
        "some category",
        List.of("some tag")
    );
    Post post = Post.builder()
        .title("some title")
        .content("some content")
        .category("some category")
        .tags(List.of(new Tag("some tag")))
        .build();
    ReflectionTestUtils.setField(post, "id", 1L);
    ReflectionTestUtils.setField(post, "createdAt", Instant.now());
    ReflectionTestUtils.setField(post, "updatedAt", Instant.now());
    PostResponseDto responseDto =  new PostResponseDto(
        post.getId(),
        post.getTitle(),
        post.getContent(),
        post.getCategory(),
        List.of(post.getTags().getFirst().getName()),
        post.getCreatedAt().toString(),
        post.getUpdatedAt().toString()
    );

    when(tagRepository.findByName("some tag")).thenReturn(Optional.of(post.getTags().getFirst()));
    when(postMapper.toEntity(requestDto, post.getTags())).thenReturn(post);
    when(postRepository.save(post)).thenReturn(post);
    when(postMapper.toDto(post)).thenReturn(responseDto);

    // act
    PostResponseDto actual = postService.create(requestDto);

    // assert
    assertNotNull(actual);
    assertEquals(post.getId(), actual.id());
    assertEquals(post.getTitle(), actual.title());
    assertEquals(post.getContent(), actual.content());
    assertEquals(post.getCategory(), actual.category());
    assertEquals(post.getTags().getFirst().getName(), actual.tags().getFirst());
    assertThat(actual.createdAt()).isEqualTo(responseDto.createdAt());
    assertThat(actual.updatedAt()).isEqualTo(responseDto.updatedAt());

    verify(tagRepository).findByName("some tag");
    verify(postMapper).toEntity(requestDto, post.getTags());
    verify(postRepository).save(post);
    verify(postMapper).toDto(post);
  }

  @Test
  void create_GivenValidRequestDtoAndNewTag_ShouldReturnResponseDto() {
    // arrange & mock
    PostRequestDto requestDto = new PostRequestDto(
        "some title",
        "some content",
        "some category",
        List.of("new tag")
    );
    Post post = Post.builder()
        .title("some title")
        .content("some content")
        .category("some category")
        .tags(List.of(new Tag("new tag")))
        .build();
    ReflectionTestUtils.setField(post, "id", 1L);
    ReflectionTestUtils.setField(post, "createdAt", Instant.now());
    ReflectionTestUtils.setField(post, "updatedAt", Instant.now());
    PostResponseDto responseDto =  new PostResponseDto(
        post.getId(),
        post.getTitle(),
        post.getContent(),
        post.getCategory(),
        List.of(post.getTags().getFirst().getName()),
        post.getCreatedAt().toString(),
        post.getUpdatedAt().toString()
    );

    when(tagRepository.findByName("new tag")).thenReturn(Optional.empty());
    when(tagRepository.save(any(Tag.class))).thenReturn(new Tag("new tag"));
    when(postMapper.toEntity(eq(requestDto), anyList())).thenReturn(post);
    when(postRepository.save(post)).thenReturn(post);
    when(postMapper.toDto(post)).thenReturn(responseDto);

    // act
    PostResponseDto actual = postService.create(requestDto);

    // assert
    assertNotNull(actual);
    assertEquals(post.getId(), actual.id());
    assertEquals(post.getTitle(), actual.title());
    assertEquals(post.getContent(), actual.content());
    assertEquals(post.getCategory(), actual.category());
    assertEquals(post.getTags().getFirst().getName(), actual.tags().getFirst());
    assertThat(actual.createdAt()).isEqualTo(responseDto.createdAt());
    assertThat(actual.updatedAt()).isEqualTo(responseDto.updatedAt());

    verify(tagRepository).findByName("new tag");
    verify(tagRepository).save(any(Tag.class));
    verify(postMapper).toEntity(eq(requestDto), anyList());
    verify(postRepository).save(post);
    verify(postMapper).toDto(post);
  }

  @Test
  void update_GivenValidIdAndRequestDto_ShouldReturnResponseDto() {
    // arrange & mock
    Long id = 1L;
    Post post = Post.builder()
        .title("some title")
        .content("some content")
        .category("some category")
        .tags(List.of(new Tag("some tag")))
        .build();
    ReflectionTestUtils.setField(post, "id", id);
    ReflectionTestUtils.setField(post, "createdAt", Instant.now());
    ReflectionTestUtils.setField(post, "updatedAt", Instant.now());
    PostRequestDto requestDto = new PostRequestDto(
        "new title",
        "new content",
        "new category",
        List.of("new tag")
    );
    PostResponseDto responseDto =  new PostResponseDto(
        post.getId(),
        requestDto.title(),
        requestDto.content(),
        requestDto.category(),
        requestDto.tags(),
        post.getCreatedAt().toString(),
        post.getUpdatedAt().toString() // this would change
    );

    when(postRepository.findById(id)).thenReturn(Optional.of(post));
    when(tagRepository.findByName("new tag")).thenReturn(Optional.empty());
    when(tagRepository.save(any(Tag.class))).thenReturn(new Tag("new tag"));
    when(postMapper.toDto(post)).thenReturn(responseDto);

    // act
    PostResponseDto actual = postService.update(id, requestDto);

    // assert
    assertNotNull(actual);
    assertEquals(post.getId(), actual.id());
    assertEquals(responseDto.title(), actual.title());
    assertEquals(responseDto.content(), actual.content());
    assertEquals(responseDto.category(), actual.category());
    assertEquals(post.getTags().getFirst().getName(), actual.tags().getFirst());
    assertThat(actual.createdAt()).isEqualTo(responseDto.createdAt());
//    assertThat(actual.updatedAt())
//        .isNotEqualTo(post.getUpdatedAt().toString());

    verify(postRepository).findById(id);
    verify(tagRepository).findByName("new tag");
    verify(tagRepository).save(any(Tag.class));
    verify(postMapper).toDto(post);
  }

  @Test
  void update_GivenInvalidId_ShouldThrowPostNotFoundException() {
    // arrange & mock
    Long id = -1L;
    when(postRepository.findById(id)).thenReturn(Optional.empty());

    // act
    assertThrows(PostNotFoundException.class, () -> postService.update(id, any(PostRequestDto.class)));

    // assert
    verify(postRepository).findById(id);
    verify(tagRepository, never()).findByName(anyString());
    verify(postMapper, never()).toDto(any(Post.class));
  }

  @Test
  void delete_GivenValidId_ShouldDeletePost() {
    Long id = 1L;
    Post post = Post.builder().build();
    ReflectionTestUtils.setField(post, "id", id);
    when(postRepository.findById(id)).thenReturn(Optional.of(post));

    postService.delete(id);

    verify(postRepository).findById(id);
    verify(postRepository).delete(post);
  }

  @Test
  void delete_GivenInvalidId_ShouldThrowPostNotFoundException() {
    Long id = -1L;
    when(postRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(PostNotFoundException.class, () -> postService.delete(id));

    verify(postRepository).findById(id);
    verify(postRepository, never()).delete(any(Post.class));
  }
}

