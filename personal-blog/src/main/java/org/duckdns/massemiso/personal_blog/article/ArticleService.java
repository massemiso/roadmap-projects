package org.duckdns.massemiso.personal_blog.article;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ArticleService {

  private final ArticleRepository repository;
  private final ArticleMapper mapper;

  @Autowired
  public ArticleService(ArticleRepository repository, ArticleMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  public List<ArticleResponseDto> getAll() {
    log.info("Fetching all articles");
    List<ArticleResponseDto> articles = repository.findAll().stream().map(mapper::toDto).toList();
    log.debug("Found {} articles", articles.size());
    return articles;
  }

  public ArticleResponseDto getById(Long id) {
    log.info("Fetching article id {}", id);
    Article article = repository.findById(id).orElseThrow();
    ArticleResponseDto responseDto = mapper.toDto(article);
    log.debug("Found article {}", responseDto);
    return responseDto;
  }

  public ArticleResponseDto create(ArticleRequestDto requestDto) {
    log.info("Creating article {}", requestDto);
    Article article = mapper.toEntity(requestDto);
    article = repository.save(article);
    ArticleResponseDto responseDto = mapper.toDto(article);
    log.debug("Created article {}", responseDto);
    return responseDto;
  }

  public ArticleResponseDto update(Long id, ArticleRequestDto requestDto) {
    log.info("Updating article {}", requestDto);
    Article article = repository.findById(id).orElseThrow();
    article.update(requestDto.title(), requestDto.content(), requestDto.dateOfPublication());
    article = repository.save(article);
    ArticleResponseDto responseDto = mapper.toDto(article);
    log.debug("Updated article {}", responseDto);
    return responseDto;
  }

  public void delete(Long id) {
    log.info("Deleting article {}", id);
    Article article = repository.findById(id).orElseThrow();
    repository.delete(article);
    log.debug("Deleted article {}", id);
  }
}
