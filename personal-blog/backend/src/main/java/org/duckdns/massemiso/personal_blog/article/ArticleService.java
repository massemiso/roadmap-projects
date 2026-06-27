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
    String methodName = "getAll";
    log.info("SERVICE: Attempting method [{}] with no arguments", methodName);

    List<ArticleResponseDto> articles =
        repository.getAll().stream().map(a -> mapper.toDto(a)).toList();

    log.info("SERVICE: Method [{}] completed successfully. Data: {}", methodName, articles);
    return articles;
  }

  public ArticleResponseDto getById(Long id) {
    String methodName = "getById";
    log.info("SERVICE: Attempting method [{}] with arguments: {}", methodName, id);

    Article entity = repository.getById(id).orElseThrow();
    ArticleResponseDto responseDto = mapper.toDto(entity);

    log.info("SERVICE: Method [{}] completed successfully. Data: {}", methodName, responseDto);
    return responseDto;
  }

  public ArticleResponseDto create(ArticleRequestDto requestDto) {
    String methodName = "create";
    log.info("SERVICE: Attempting method [{}] with arguments: {}", methodName, "");

    Article article = mapper.toEntity(requestDto);
    article = repository.save(article);
    ArticleResponseDto responseDto = mapper.toDto(article);

    log.info("SERVICE: Method [{}] completed successfully. Data: {}", methodName, responseDto);
    return responseDto;
  }

  public ArticleResponseDto update(Long id, ArticleRequestDto requestDto) {
    String methodName = "update";
    log.info("SERVICE: Attempting method [{}] with arguments: {}, {}", methodName, id, requestDto);

    Article article = repository.getById(id).orElseThrow();
    article.setTitle(requestDto.title());
    article.setContent(requestDto.content());
    article.setDateOfPublication(requestDto.dateOfPublication());
    article = repository.update(article);
    ArticleResponseDto responseDto = mapper.toDto(article);

    log.info("SERVICE: Method [{}] completed successfully. Data: {}", methodName, responseDto);
    return responseDto;
  }

  public void delete(Long id) {
    String methodName = "delete";
    log.info("SERVICE: Attempting method [{}] with arguments: {}", methodName, id);

    repository.delete(id);

    log.info("SERVICE: Method [{}] completed successfully");
  }
}
