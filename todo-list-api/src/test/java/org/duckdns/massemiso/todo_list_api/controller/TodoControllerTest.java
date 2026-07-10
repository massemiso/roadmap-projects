package org.duckdns.massemiso.todo_list_api.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.duckdns.massemiso.todo_list_api.dto.AuthRequestDto;
import org.duckdns.massemiso.todo_list_api.dto.TodoRequestDto;
import org.duckdns.massemiso.todo_list_api.repository.TodoRepository;
import org.duckdns.massemiso.todo_list_api.repository.UserRepository;
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
class TodoControllerTest {

  @LocalServerPort
  private Integer port;

  @Container
  @ServiceConnection
  static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private TodoRepository todoRepository;

  private String token;

  @BeforeEach
  void setUp() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;
    todoRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();

    AuthRequestDto authRequest = new AuthRequestDto("Test", "test@test.com", "password");
    given().contentType(ContentType.JSON).body(authRequest).post("/register");

    token = given()
        .contentType(ContentType.JSON)
        .body(authRequest)
        .post("/login")
        .jsonPath()
        .getString("token");
  }

  @Test
  void create_ShouldReturn201_WhenRequestIsValid() {
    TodoRequestDto request = new TodoRequestDto("Title", "Desc", false);
    given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/todos")
        .then()
        .statusCode(HttpStatus.CREATED.value())
        .body("title", is("Title"));
  }

  @Test
  void create_ShouldReturn400_WhenInvalidRequest() {
    TodoRequestDto request = new TodoRequestDto("", "", false);
    given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/todos")
        .then()
        .statusCode(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  void findById_ShouldReturn200_WhenTodoExists() {
    // Create a todo first
    Integer id = given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body(new TodoRequestDto("Task", "Desc", false))
        .post("/todos")
        .jsonPath()
        .getInt("id");

    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/todos/{id}", id)
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("id", is(id));
  }

  @Test
  void findById_ShouldReturn404_WhenTodoNotFound() {
    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/todos/{id}", 999)
        .then()
        .statusCode(HttpStatus.NOT_FOUND.value());
  }

  @Test
  void update_ShouldReturn200_WhenRequestIsValid() {
    Integer id = given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body(new TodoRequestDto("Task", "Desc", false))
        .post("/todos")
        .jsonPath()
        .getInt("id");

    given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body(new TodoRequestDto("Updated", "Updated", true))
        .when()
        .put("/todos/{id}", id)
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("title", is("Updated"))
        .body("completed", is(true));
  }

  @Test
  void delete_ShouldReturn204_WhenTodoDeleted() {
    Integer id = given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body(new TodoRequestDto("Task", "Desc", false))
        .post("/todos")
        .jsonPath()
        .getInt("id");

    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .delete("/todos/{id}", id)
        .then()
        .statusCode(HttpStatus.NO_CONTENT.value());
  }
}
