package org.duckdns.massemiso.blogging_platform_api.controller;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.duckdns.massemiso.blogging_platform_api.dto.PostRequestDto;
import org.duckdns.massemiso.blogging_platform_api.persistence.Post;
import org.duckdns.massemiso.blogging_platform_api.persistence.Tag;
import org.duckdns.massemiso.blogging_platform_api.persistence.repository.PostRepository;
import org.duckdns.massemiso.blogging_platform_api.persistence.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class PostControllerTest {

  @LocalServerPort
  private Integer port;

  @Container
  @ServiceConnection
  static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

  private final PostRepository postRepository;
  private final TagRepository tagRepository;

  @Autowired
  public PostControllerTest(PostRepository postRepository, TagRepository tagRepository) {
    this.postRepository = postRepository;
    this.tagRepository = tagRepository;
  }

  @BeforeEach
  void setUp() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;
    postRepository.deleteAllInBatch();
    tagRepository.deleteAllInBatch();
  }

  @Test
  void getAll_ShouldReturnAListOfSavedPosts() {
    List<Post> posts = this.insertSomeDefaultPosts();
    given()
        .contentType(ContentType.JSON)
    .when()
        .get("/posts")
    .then()
        .statusCode(HttpStatus.OK.value())
        .body("$", hasSize(posts.size()))
        .body("[0].id", is(posts.getFirst().getId().intValue()))
        .body("[2].id", is(posts.getLast().getId().intValue()));
  }

  @Test
  void getAll_GivenTerm_ShouldReturnAListOfSavedPostsFilteredByTerm() {
    String term = "title 1";
    List<Post> posts = this.insertSomeDefaultPosts();
    given()
        .contentType(ContentType.JSON)
        .queryParam("term", term)
    .when()
        .get("/posts")
    .then()
        .statusCode(HttpStatus.OK.value())
        .body("$", hasSize(1))
        .body("[0].id", is(posts.getFirst().getId().intValue()))
        .body("[0].title", is(posts.getFirst().getTitle()));
  }

  @Test
  void getAll_GivenTag_ShouldReturnAListOfSavedPostsFilteredByTag() {
    String tag = "Code";
    List<Post> posts = this.insertSomeDefaultPosts();
    given()
        .contentType(ContentType.JSON)
        .queryParam("tag", tag)
    .when()
        .get("/posts")
    .then()
        .statusCode(HttpStatus.OK.value())
        .body("$", hasSize(2))
        .body("[0].id", is(posts.get(1).getId().intValue()))
        .body("[0].tags[0]", is(posts.get(1).getTags().getFirst().getName()));
  }

  @Test
  void getAll_GivenTermAndTag_ShouldReturnAListOfSavedPostsFilteredByTermAndTag() {
    String term = "title 2";
    String tag = "Code";
    List<Post> posts = this.insertSomeDefaultPosts();
    given()
        .contentType(ContentType.JSON)
        .queryParam("term", term)
        .queryParam("tag", tag)
    .when()
        .get("/posts")
    .then()
        .statusCode(HttpStatus.OK.value())
        .body("$", hasSize(1))
        .body("[0].id", is(posts.get(1).getId().intValue()))
        .body("[0].title", is(posts.get(1).getTitle()))
        .body("[0].tags[0]", is(posts.get(1).getTags().getFirst().getName()));
  }

  @Test
  void getById_GivenValidId_ShouldReturnSavedPost() {
    Post post = this.insertSomeDefaultPosts().getFirst();
    Integer id = post.getId().intValue();
    given()
        .contentType(ContentType.JSON)
    .when()
        .get("/posts/{id}", id)
    .then()
        .statusCode(HttpStatus.OK.value())
        .body("id", is(id))
        .body("title", is(post.getTitle()))
        .body("content", is(post.getContent()))
        .body("category",  is(post.getCategory()))
        .body("tags[0]", is(post.getTags().getFirst().getName()))
        .body("createdAt", notNullValue())
        .body("updatedAt", notNullValue());
  }

  @Test
  void getById_GivenInvalidId_ShouldReturn404NotFound() {
    Integer id = -1;
    given()
        .contentType(ContentType.JSON)
    .when()
        .get("/posts/{id}", id)
    .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body("timestamp", notNullValue())
        .body("timestamp", containsStringIgnoringCase(LocalDate.now().toString()))
        .body("status", is(HttpStatus.NOT_FOUND.value()))
        .body("message", is("Post not found"))
        .body("details", notNullValue());
  }

  @Test
  void create_GivenValidRequestDto_ShouldReturnNewPost() {
    PostRequestDto requestDto = new PostRequestDto(
        "new title",
        "new content",
        "new category",
        List.of("Code"));
    given()
        .contentType(ContentType.JSON)
    .when()
        .body(requestDto)
        .post("/posts")
    .then()
        .statusCode(HttpStatus.CREATED.value())
        .body("id", notNullValue())
        .body("title", is(requestDto.title()))
        .body("content", is(requestDto.content()))
        .body("category", is(requestDto.category()))
        .body("tags[0]", is(requestDto.tags().getFirst()))
        .body("createdAt", notNullValue())
        .body("updatedAt", notNullValue());
  }

  @Test
  void create_GivenInvalidRequestDto_ShouldReturn400BadRequest() {
    PostRequestDto requestDto = new PostRequestDto(
        "",
        "",
        "",
        List.of());
    given()
        .contentType(ContentType.JSON)
    .when()
        .body(requestDto)
        .post("/posts")
    .then()
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .body("timestamp", notNullValue())
        .body("timestamp", containsStringIgnoringCase(LocalDate.now().toString()))
        .body("status", is(HttpStatus.BAD_REQUEST.value()))
        .body("message", is("Validation Failed"))
        .body("details", notNullValue());
  }

  @Test
  void update_GivenValidIdAndRequestDto_ShouldReturnUpdatedPost() {
    Post post = this.insertSomeDefaultPosts().getFirst();
    Integer id = post.getId().intValue();
    PostRequestDto requestDto = new PostRequestDto(
        "new title",
        "new content",
        "new category",
        List.of("Code"));
    given()
        .contentType(ContentType.JSON)
    .when()
        .body(requestDto)
        .put("/posts/{id}", id)
    .then()
        .statusCode(HttpStatus.OK.value())
        .body("id", is(id))
        .body("title", is(requestDto.title()))
        .body("content", is(requestDto.content()))
        .body("category", is(requestDto.category()))
        .body("tags[0]", is(requestDto.tags().getFirst()))
        .body("createdAt", notNullValue())
        .body("updatedAt", notNullValue());
  }

  @Test
  void update_GivenInvalidId_ShouldReturn404NotFound() {
    Integer id = -1;
    PostRequestDto requestDto = new PostRequestDto(
        "new title",
        "new content",
        "new category",
        List.of("Code"));
    given()
        .contentType(ContentType.JSON)
        .when()
    .body(requestDto)
        .put("/posts/{id}", id)
    .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body("timestamp", notNullValue())
        .body("timestamp", containsStringIgnoringCase(LocalDate.now().toString()))
        .body("status", is(HttpStatus.NOT_FOUND.value()))
        .body("message", is("Post not found"))
        .body("details", notNullValue());
  }

  @Test
  void update_GivenInvalidRequestDto_ShouldReturn400BadRequest() {
    Integer id = 1;
    PostRequestDto requestDto = new PostRequestDto(
        "",
        "",
        "",
        List.of());
    given()
        .contentType(ContentType.JSON)
    .when()
        .body(requestDto)
        .put("/posts/{id}", id)
    .then()
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .body("timestamp", notNullValue())
        .body("timestamp", containsStringIgnoringCase(LocalDate.now().toString()))
        .body("status", is(HttpStatus.BAD_REQUEST.value()))
        .body("message", is("Validation Failed"))
        .body("details", notNullValue());
  }

  @Test
  void delete_GivenValidId_ShouldReturn204NoContent() {
    Integer id = this.insertSomeDefaultPosts().getFirst().getId().intValue();
    given()
        .contentType(ContentType.JSON)
    .when()
        .delete("/posts/{id}", id)
    .then()
        .statusCode(HttpStatus.NO_CONTENT.value());
  }

  @Test
  void delete_GivenInvalidId_ShouldReturn404NotFound() {
    given()
        .contentType(ContentType.JSON)
    .when()
        .delete("/posts/{id}", -1)
    .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body("timestamp", notNullValue())
        .body("timestamp", containsStringIgnoringCase(LocalDate.now().toString()))
        .body("status", is(HttpStatus.NOT_FOUND.value()))
        .body("message", is("Post not found"))
        .body("details", notNullValue());
  }

  @Transactional
  private List<Post> insertSomeDefaultPosts() {
    Tag art = tagRepository.save(new Tag("Art"));
    Tag code = tagRepository.save(new Tag("Code"));
    Post post1 = postRepository.save(Post.builder()
        .title("some title 1")
        .content("some content 1")
        .category("some category 1")
        .tags(List.of(art))
        .build());
    Post post2 = postRepository.save(Post.builder()
        .title("some title 2")
        .content("some content 2")
        .category("some category 2")
        .tags(List.of(code))
        .build());
    Post post3 = postRepository.save(Post.builder()
        .title("some title 3")
        .content("some content 3")
        .category("some category 3")
        .tags(List.of(art, code))
        .build());
    return List.of(post1, post2, post3);
  }
}