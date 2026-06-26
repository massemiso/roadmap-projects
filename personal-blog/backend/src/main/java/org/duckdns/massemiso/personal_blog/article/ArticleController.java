package org.duckdns.massemiso.personal_blog.article;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class ArticleController {

  private final ArticleService service;

  @Autowired
  public ArticleController(ArticleService service) {
    this.service = service;
  }

  @GetMapping("/article/{id}")
  public String getById(@PathVariable(name = "id", required = true) Long id, Model model) {
    log.info("REQUEST: getById({})", id);

    ArticleResponseDto responseDto = service.getById(id);
    model.addAttribute("dto", responseDto);

    log.info("RESPONSE: {}", responseDto);
    return "article";
  }

  @RequestMapping("/new")
  public String create() {
    ArticleRequestDto dto =
        new ArticleRequestDto("xx title xx", "xxxxx content xxxx", LocalDateTime.now());

    log.info("REQUEST: create({})", dto);
    ArticleResponseDto responseDto = service.create(dto);

    log.info("RESPONSE: {}", responseDto);
    return "index";
  }
}
