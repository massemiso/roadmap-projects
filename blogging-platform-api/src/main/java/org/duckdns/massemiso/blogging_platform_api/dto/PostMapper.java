package org.duckdns.massemiso.blogging_platform_api.dto;

import java.util.List;
import org.duckdns.massemiso.blogging_platform_api.persistence.Post;
import org.duckdns.massemiso.blogging_platform_api.persistence.Tag;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

  public PostResponseDto toDto(Post post) {
    List<String> tags = post.getTags().stream()
        .map(Tag::getName)
        .toList();
    return new PostResponseDto(
        post.getId(),
        post.getTitle(),
        post.getContent(),
        post.getCategory(),
        tags
    );
  }

  public Post toEntity(PostRequestDto requestDto) {
    List<Tag> tags = requestDto.tags().stream()
        .map(t -> Tag.builder().name(t).build())
        .toList();
    return Post.builder()
        .title(requestDto.title())
        .content(requestDto.content())
        .category(requestDto.category())
        .tags(tags)
        .build();
  }

}
