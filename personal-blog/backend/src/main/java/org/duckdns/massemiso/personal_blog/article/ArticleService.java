package org.duckdns.massemiso.personal_blog.article;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ArticleService {

  private final ArticleRepository repository;
  private final ArticleMapper mapper;

  @Autowired
  public ArticleService(ArticleRepository repository, ArticleMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
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
}
