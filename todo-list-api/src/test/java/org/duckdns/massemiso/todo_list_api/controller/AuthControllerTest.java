package org.duckdns.massemiso.todo_list_api.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.duckdns.massemiso.todo_list_api.dto.AuthRequestDto;
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
class AuthControllerTest {

  @LocalServerPort
  private Integer port;

  @Container
  @ServiceConnection
  static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;
    userRepository.deleteAllInBatch();
  }

  @Test
  void register_ShouldReturn201_WhenRequestIsValid() {
    AuthRequestDto request = new AuthRequestDto("Test", "test@test.com", "password");
    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/register")
        .then()
        .statusCode(HttpStatus.CREATED.value())
        .body("token", notNullValue());
  }

  @Test
  void register_ShouldReturn409_WhenEmailAlreadyExists() {
    AuthRequestDto request = new AuthRequestDto("Test", "test@test.com", "password");
    // Pre-register
    given().contentType(ContentType.JSON).body(request).post("/register");

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/register")
        .then()
        .statusCode(HttpStatus.CONFLICT.value())
        .body("message", is("Email already exists"));
  }

  @Test
  void register_ShouldReturn400_WhenInvalidRequest() {
    AuthRequestDto request = new AuthRequestDto("", "", "");
    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/register")
        .then()
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .body("message", is("Validation Failed"));
  }

  @Test
  void login_ShouldReturn200_WhenCredentialsValid() {
    AuthRequestDto registerReq = new AuthRequestDto("Test", "login@test.com", "password");
    given().contentType(ContentType.JSON).body(registerReq).post("/register");

    given()
        .contentType(ContentType.JSON)
        .body(registerReq)
        .when()
        .post("/login")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("token", notNullValue());
  }

  @Test
  void login_ShouldReturn404_WhenUserDoesNotExist() {
    AuthRequestDto request = new AuthRequestDto("Test", "unknown@test.com", "password");
    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/login")
        .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body("message", is("Email not found"));
  }
}
