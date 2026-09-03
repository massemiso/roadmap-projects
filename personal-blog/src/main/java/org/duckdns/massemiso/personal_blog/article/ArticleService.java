package org.duckdns.massemiso.personal_blog.article;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.duckdns.massemiso.personal_blog.utils.MarkdownUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ArticleService {

  private final ArticleRepository repository;
  private final ArticleMapper mapper;
  private final MarkdownUtils markdownUtils;

  @Autowired
  public ArticleService(
      ArticleRepository repository, ArticleMapper mapper, MarkdownUtils markdownUtils) {
    this.repository = repository;
    this.mapper = mapper;
    this.markdownUtils = markdownUtils;
  }

  public Page<ArticleResponseDto> getAll(Pageable pageable, ArticleFilterDto articleFilterDto) {
    log.info("Fetching {} articles for page {}", pageable.getPageSize(), pageable.getPageNumber());
    Specification<Article> spec = articleFilterDto.getSpecification();
    Page<ArticleResponseDto> page = repository.findAll(spec, pageable).map(mapper::toDto);
    log.debug("Found {}", page);
    return page;
  }

  public ArticleResponseDto getById(Long id) {
    log.info("Fetching article id {}", id);
    Article article = repository.findById(id).orElseThrow();
    ArticleResponseDto responseDto =
        mapper.toDto(article, markdownUtils.markdownToHtml(article.getContent()));
    log.debug("Found article {}", responseDto);
    return responseDto;
  }

  public ArticleResponseDto getArticleForEdit(Long id) {
    log.info("Fetching article id {}", id);
    Article article = repository.findById(id).orElseThrow();
    ArticleResponseDto responseDto = mapper.toDto(article);
    log.debug("Found article {}", responseDto);
    return responseDto;
  }

  @Transactional
  public ArticleResponseDto create(ArticleRequestDto requestDto) {
    log.info("Creating article {}", requestDto);
    Article article = mapper.toEntity(requestDto);
    article = repository.save(article);
    ArticleResponseDto responseDto =
        mapper.toDto(article, markdownUtils.markdownToHtml(article.getContent()));
    log.debug("Created article {}", responseDto);
    return responseDto;
  }

  @Transactional
  public ArticleResponseDto update(Long id, ArticleRequestDto requestDto) {
    log.info("Updating article {}", requestDto);
    Article article = repository.findById(id).orElseThrow();
    article.update(requestDto.title(), requestDto.content(), requestDto.dateOfPublication());
    article = repository.save(article);
    ArticleResponseDto responseDto =
        mapper.toDto(article, markdownUtils.markdownToHtml(article.getContent()));
    log.debug("Updated article {}", responseDto);
    return responseDto;
  }

  @Transactional
  public void delete(Long id) {
    log.info("Deleting article {}", id);
    Article article = repository.findById(id).orElseThrow();
    repository.delete(article);
    log.debug("Deleted article {}", id);
  }
}
