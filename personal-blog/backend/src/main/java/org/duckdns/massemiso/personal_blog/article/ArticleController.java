package org.duckdns.massemiso.personal_blog.article;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class ArticleController {

  private final ArticleService service;

  @Autowired
  public ArticleController(ArticleService service) {
    this.service = service;
  }

  @GetMapping("/home")
  public String home(Model model) {
    log.info("REQUEST: home");

    List<ArticleResponseDto> articles = service.getAll();
    model.addAttribute("articles", articles);

    log.info("RESPONSE: {}", articles);
    return "home";
  }

  @GetMapping("/admin")
  public String adminDashboard(Model model) {
    log.info("REQUEST: admin");

    List<ArticleResponseDto> articles = service.getAll();
    model.addAttribute("articles", articles);

    log.info("RESPONSE: {}", articles);
    return "admin";
  }

  @GetMapping("/article/{id}")
  public String getById(@PathVariable(name = "id", required = true) Long id, Model model) {
    log.info("REQUEST: getById({})", id);

    ArticleResponseDto responseDto = service.getById(id);
    model.addAttribute("dto", responseDto);

    log.info("RESPONSE: {}", responseDto);
    return "article";
  }

  @GetMapping("/new")
  public String formNew() {
    log.info("REQUEST: formNew");
    return "new";
  }

  @PostMapping("/article")
  public String create(ArticleRequestDto dto) {
    log.info("REQUEST: create({})", dto);
    ArticleResponseDto responseDto = service.create(dto);

    log.info("RESPONSE: {}", responseDto);
    return "admin";
  }

  @GetMapping("/edit/{id}")
  public String formEdit(@PathVariable(name = "id", required = true) Long id, Model model) {
    log.info("REQUEST: formDelete");

    ArticleResponseDto responseDto = service.getById(id);
    model.addAttribute("dto", responseDto);
    model.addAttribute("id", id);

    log.info("RESPONSE: {}", responseDto);
    return "edit";
  }

  @PostMapping("/update/{id}")
  public String update(@PathVariable(name = "id", required = true) Long id, ArticleRequestDto dto) {
    log.info("REQUEST: update({})", dto);
    ArticleResponseDto responseDto = service.update(id, dto);

    log.info("RESPONSE: {}", responseDto);
    return "admin";
  }

  @GetMapping("/delete/{id}")
  public String delete(@PathVariable(name = "id", required = true) Long id) {
    log.info("REQUEST: delete({})", id);
    service.delete(id);

    log.info("RESPONSE: {}", "Article " + id + " deleted succesfully");
    return "admin";
  }
}
