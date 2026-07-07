package org.duckdns.massemiso.blogging_platform_api.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.duckdns.massemiso.blogging_platform_api.dto.PostMapper;
import org.duckdns.massemiso.blogging_platform_api.dto.PostRequestDto;
import org.duckdns.massemiso.blogging_platform_api.dto.PostResponseDto;
import org.duckdns.massemiso.blogging_platform_api.exception.PostNotFoundException;
import org.duckdns.massemiso.blogging_platform_api.persistence.Post;
import org.duckdns.massemiso.blogging_platform_api.persistence.Tag;
import org.duckdns.massemiso.blogging_platform_api.persistence.repository.PostRepository;
import org.duckdns.massemiso.blogging_platform_api.persistence.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PostService {

  private final PostRepository postRepository;
  private final PostMapper postMapper;
  private final TagRepository tagRepository;

  @Autowired
  public PostService(PostRepository postRepository, PostMapper postMapper, TagRepository tagRepository){
    this.postRepository = postRepository;
    this.postMapper = postMapper;
    this.tagRepository = tagRepository;
  }

  public List<PostResponseDto> getAll() {
    log.info("Getting all posts");

    List<PostResponseDto> list = this.postRepository.findAll().stream()
        .map(postMapper::toDto)
        .toList();
    log.info("Returning all posts {}...",
        (list.size() > 3) ? list.subList(0, 3) : list);

    return list;
  }

  public PostResponseDto getById(Long id) {
    log.info("Getting post id {}", id);

    Post entity = this.postRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException(id));
    PostResponseDto dto = this.postMapper.toDto(entity);

    log.info("Returning post {}...", dto);
    return dto;
  }

  @Transactional
  public PostResponseDto create(PostRequestDto requestDto) {
    log.info("Creating post {}", requestDto);

    List<Tag> tags = requestDto.tags().stream()
        .map(name -> tagRepository.findByName(name)
            .orElseGet(() -> Tag.builder().name(name).build()))
        .toList();
    Post entity = this.postMapper.toEntity(requestDto, tags);
    entity = this.postRepository.save(entity);
    PostResponseDto dto = this.postMapper.toDto(entity);

    log.info("Returning post {}...", dto);
    return dto;
  }

  @Transactional
  public PostResponseDto update(Long id, PostRequestDto requestDto) {
    log.info("Updating post {} with {}", id, requestDto);

    Post entity = this.postRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException(id));
    List<Tag> otherTags = requestDto.tags().stream()
        .map(name -> tagRepository.findByName(name)
            .orElseGet(() -> Tag.builder().name(name).build()))
        .toList();
    entity.update(requestDto.title(), requestDto.content(), requestDto.category(), otherTags);
    PostResponseDto dto = this.postMapper.toDto(entity);

    log.info("Returning post {}...", dto);
    return dto;
  }

  @Transactional
  public void delete(Long id) {
    log.info("Deleting post {}", id);

    Post entity = this.postRepository.findById(id).orElseThrow();
    this.postRepository.delete(entity);

    log.info("Deleting post {}...", entity);
  }
}
