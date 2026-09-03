package org.duckdns.massemiso.personal_blog.article;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

@AutoConfigureMockMvc
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ArticleService articleService;

  @Test
  void root_ShouldRedirectToHome() throws Exception {
    // Act & Assert
    mockMvc.perform(get("/"))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/home"));
  }

  @Test
  void home_ShouldReturnHomeViewWithArticles() throws Exception {
    // Arrange
    List<ArticleResponseDto> articles = List.of(
        new ArticleResponseDto(1L, "Title", "Content", "Jun 30, 2026")
    );
    Page<ArticleResponseDto> page = new PageImpl<>(articles);
    when(articleService.getAll(any(Pageable.class), any(ArticleFilterDto.class))).thenReturn(page);

    // Act & Assert
    mockMvc.perform(get("/home"))
        .andExpect(status().isOk())
        .andExpect(view().name("home"))
        .andExpect(model().attributeExists("articles"))
        .andExpect(model().attribute("articles", articles));
  }

  @Test
  void adminDashboard_ShouldReturnAdminViewWithArticles() throws Exception {
    // Arrange
    List<ArticleResponseDto> articles = List.of(
        new ArticleResponseDto(1L, "Title", "Content", "Jun 30, 2026")
    );
    Page<ArticleResponseDto> page = new PageImpl<>(articles);
    when(articleService.getAll(any(Pageable.class), any(ArticleFilterDto.class))).thenReturn(page);

    // Act & Assert
    mockMvc.perform(get("/admin"))
        .andExpect(status().isOk())
        .andExpect(view().name("admin"))
        .andExpect(model().attributeExists("articles"))
        .andExpect(model().attribute("articles", articles));
  }

  @Test
  void formNew_ShouldReturnNewView() throws Exception {
    // Act & Assert
    mockMvc.perform(get("/new"))
        .andExpect(status().isOk())
        .andExpect(view().name("new"));
  }

  @Test
  void create_GivenValidRequestDto_ShouldRedirectToAdmin() throws Exception {
    // Arrange
    ArticleRequestDto requestDto = new ArticleRequestDto("Title", "Content", LocalDate.now());
    ArticleResponseDto responseDto = new ArticleResponseDto(1L, "Title", "Content",
        LocalDate.now().toString());
    when(articleService.create(requestDto)).thenReturn(responseDto);

    // Act & Assert
    mockMvc.perform(post("/article")
            .params(
                MultiValueMap.fromSingleValue(Map.of(
                    "title", "Title",
                    "content", "content",
                    "dateOfPublication", LocalDate.now().toString()))))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/admin"));
  }

  @Test
  void create_GivenInvalidRequestDto_ShouldThrowExceptionAndReturnErrorView() throws Exception {
    // Act & Assert
    mockMvc.perform(post("/article")
            .params(
                MultiValueMap.fromSingleValue(Map.of(
                    "title", "",
                    "content", ""))))
        .andExpect(status().isBadRequest())
        .andExpect(view().name("error"));
  }

  @Test
  void formEdit_ShouldReturnEditViewWithResponseDtoAndId() throws Exception {
    // Arrange
    ArticleResponseDto dto = new ArticleResponseDto(1L, "Title", "Content", "Jun 30, 2026");
    String route = String.format("/edit/%d", dto.id());
    when(articleService.getArticleForEdit(dto.id())).thenReturn(dto);

    // Act & Assert
    mockMvc.perform(get(route))
        .andExpect(status().isOk())
        .andExpect(view().name("edit"))
        .andExpect(model().attributeExists("dto"))
        .andExpect(model().attributeExists("id"))
        .andExpect(model().attribute("dto", dto))
        .andExpect(model().attribute("id", dto.id()));
  }

  @Test
  void update_GivenValidRequestDto_ShouldRedirectToAdmin() throws Exception {
    // Arrange
    Long id = 1L;
    ArticleRequestDto requestDto = new ArticleRequestDto("Title", "Content", LocalDate.now());
    ArticleResponseDto responseDto = new ArticleResponseDto(1L, "Title", "Content",
        LocalDate.now().toString());
    String route = String.format("/update/%d", id);
    when(articleService.update(id, requestDto)).thenReturn(responseDto);

    // Act & Assert
    mockMvc.perform(post(route)
            .params(
                MultiValueMap.fromSingleValue(Map.of(
                    "title", "Title",
                    "content", "content",
                    "dateOfPublication", LocalDate.now().toString()))))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/admin"));
  }

  @Test
  void update_GivenInvalidRequestDto_ShouldThrowExceptionAndReturnErrorView() throws Exception {
    // Act & Assert
    mockMvc.perform(post("/update/1")
            .params(
                MultiValueMap.fromSingleValue(Map.of(
                    "title", "",
                    "content", ""))))
        .andExpect(status().isBadRequest())
        .andExpect(view().name("error"));
  }

  @Test
  void update_GivenInvalidId_ShouldThrowExceptionAndReturnErrorView() throws Exception {
    Long id = 1L;
    ArticleRequestDto requestDto = new ArticleRequestDto("Title", "Content", LocalDate.now());
    when(articleService.update(id, requestDto)).thenThrow(NoSuchElementException.class);
    mockMvc.perform(post("/update/1")
            .params(
                MultiValueMap.fromSingleValue(Map.of(
                    "title", "Title",
                    "content", "Content",
                    "dateOfPublication", LocalDate.now().toString()))))
        .andExpect(status().isNotFound())
        .andExpect(view().name("error"));
  }

  @Test
  void delete_GivenValidId_ShouldRedirectToAdmin() throws Exception {
    // Arrange
    Long id = 1L;
    String route = String.format("/delete/%d", id);

    // mock
    doNothing().when(articleService).delete(id);

    // Act & Assert
    mockMvc.perform(get(route))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/admin"));

    verify(articleService).delete(id);
  }

  @Test
  void delete_GivenInvalidId_ShouldThrowExceptionAndReturnErrorView() throws Exception {
    Long id = 1L;
    doThrow(NoSuchElementException.class).when(articleService).delete(id);
    mockMvc.perform(get("/delete/1"))
        .andExpect(status().isNotFound())
        .andExpect(view().name("error"));
  }
}
