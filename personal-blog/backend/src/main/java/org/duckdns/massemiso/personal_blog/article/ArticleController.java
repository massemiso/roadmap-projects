package org.duckdns.massemiso.personal_blog.article;

import jakarta.validation.Valid;
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

  @GetMapping("/")
  public String goToHome() {
    log.info("Redirecting to home");
    return "redirect:/home";
  }

  @GetMapping("/home")
  public String home(Model model) {
    log.info("Fetching all articles");
    List<ArticleResponseDto> articles = service.getAll();
    model.addAttribute("articles", articles);
    return "home";
  }

  @GetMapping("/admin")
  public String adminDashboard(Model model) {
    log.info("Fetching all articles");
    List<ArticleResponseDto> articles = service.getAll();
    model.addAttribute("articles", articles);
    return "admin";
  }

  @GetMapping("/article/{id}")
  public String getById(@PathVariable(name = "id", required = true) Long id, Model model) {
    log.info("Fetching article {}", id);
    ArticleResponseDto responseDto = service.getById(id);
    model.addAttribute("dto", responseDto);
    return "article";
  }

  @GetMapping("/new")
  public String formNew() {
    log.info("Serving new.html");
    return "new";
  }

  @PostMapping("/article")
  public String create(@Valid ArticleRequestDto dto) {
    log.info("Creating new article: {}", dto.title());
    service.create(dto);
    return "redirect:/admin";
  }

  @GetMapping("/edit/{id}")
  public String formEdit(@PathVariable(name = "id", required = true) Long id, Model model) {
    log.info("Serving edit.html");
    ArticleResponseDto responseDto = service.getById(id);
    model.addAttribute("dto", responseDto);
    model.addAttribute("id", id);
    return "edit";
  }

  @PostMapping("/update/{id}")
  public String update(
      @PathVariable(name = "id", required = true) Long id, @Valid ArticleRequestDto dto) {
    log.info("Updating article: {}", id);
    ArticleResponseDto responseDto = service.update(id, dto);
    return "redirect:/admin";
  }

  @GetMapping("/delete/{id}")
  public String delete(@PathVariable(name = "id", required = true) Long id) {
    log.info("Deleting article {}", id);
    service.delete(id);
    return "redirect:/admin";
  }
}
