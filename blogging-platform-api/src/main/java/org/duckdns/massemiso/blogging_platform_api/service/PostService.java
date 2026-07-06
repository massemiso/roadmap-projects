package org.duckdns.massemiso.blogging_platform_api.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.duckdns.massemiso.blogging_platform_api.dto.PostMapper;
import org.duckdns.massemiso.blogging_platform_api.dto.PostRequestDto;
import org.duckdns.massemiso.blogging_platform_api.dto.PostResponseDto;
import org.duckdns.massemiso.blogging_platform_api.persistence.Post;
import org.duckdns.massemiso.blogging_platform_api.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PostService {

  private final PostRepository postRepository;
  private final PostMapper postMapper;

  @Autowired
  public PostService(PostRepository postRepository, PostMapper postMapper){
    this.postRepository = postRepository;
    this.postMapper = postMapper;
  }

  public List<PostResponseDto> getAll() {
    log.info("SERVICE: Getting all posts");

    List<PostResponseDto> list = this.postRepository.findAll().stream()
        .map(postMapper::toDto)
        .toList();
    log.info("SERVICE: Returning all posts {}...",
        (list.size() > 3) ? list.subList(0, 3) : list);

    return list;
  }

  public PostResponseDto getById(Long id) {
    log.info("SERVICE: Getting post id {}", id);

    Post entity = this.postRepository.findById(id).orElseThrow();
    PostResponseDto dto = this.postMapper.toDto(entity);

    log.info("SERVICE: Returning post {}...", dto);
    return dto;
  }

  @Transactional
  public PostResponseDto create(PostRequestDto requestDto) {
    log.info("SERVICE: Creating post {}", requestDto);

    Post entity = this.postMapper.toEntity(requestDto);
    entity = this.postRepository.save(entity);
    PostResponseDto dto = this.postMapper.toDto(entity);

    log.info("SERVICE: Returning post {}...", dto);
    return dto;
  }

  @Transactional
  public PostResponseDto update(Long id, PostRequestDto requestDto) {
    log.info("SERVICE: Updating post {} with {}", id, requestDto);

    Post entity = this.postRepository.findById(id).orElseThrow();
    entity.update(postMapper.toEntity(requestDto));
    entity = this.postRepository.save(entity);
    PostResponseDto dto = this.postMapper.toDto(entity);

    log.info("SERVICE: Returning post {}...", dto);
    return dto;
  }

  @Transactional
  public void delete(Long id) {
    log.info("SERVICE: Deleting post {}", id);

    Post entity = this.postRepository.findById(id).orElseThrow();
    this.postRepository.delete(entity);

    log.info("SERVICE: Deleting post {}...", entity);
  }
}
