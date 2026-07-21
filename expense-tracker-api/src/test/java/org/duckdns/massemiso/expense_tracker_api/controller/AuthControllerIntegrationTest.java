package org.duckdns.massemiso.expense_tracker_api.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.duckdns.massemiso.expense_tracker_api.ExpenseTrackerApiApplication;
import org.duckdns.massemiso.expense_tracker_api.TestcontainersConfiguration;
import org.duckdns.massemiso.expense_tracker_api.dto.AuthRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(classes = ExpenseTrackerApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {

  @LocalServerPort
  private Integer port;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
  }

  @Test
  void register_ShouldReturn201_WhenValid() {
    AuthRequestDto request = new AuthRequestDto("newUser", "password123");

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .post("/auth/register")
        .then()
        .statusCode(201)
        .body("token", notNullValue());
  }

  @Test
  void register_ShouldReturn400_WhenInvalid() {
    AuthRequestDto request = new AuthRequestDto("", "123");

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .post("/auth/register")
        .then()
        .statusCode(400);
  }

  @Test
  void register_ShouldReturn409_WhenUserExists() {
    String username = "existingUser";
    AuthRequestDto request = new AuthRequestDto(username, "password123");

    given().contentType(ContentType.JSON).body(request).post("/auth/register");

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .post("/auth/register")
        .then()
        .statusCode(409);
  }

  @Test
  void login_ShouldReturn200_WhenValid() {
    String username = "loginUser";
    AuthRequestDto request = new AuthRequestDto(username, "password123");
    given().contentType(ContentType.JSON).body(request).post("/auth/register");

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .post("/auth/login")
        .then()
        .statusCode(200)
        .body("token", notNullValue());
  }

  @Test
  void login_ShouldReturn401_WhenWrongPassword() {
    String username = "wrongPassUser";
    AuthRequestDto request = new AuthRequestDto(username, "password123");
    given().contentType(ContentType.JSON).body(request).post("/auth/register");

    AuthRequestDto loginRequest = new AuthRequestDto(username, "wrongPassword");
    given()
        .contentType(ContentType.JSON)
        .body(loginRequest)
        .post("/auth/login")
        .then()
        .statusCode(401);
  }

  @Test
  void login_ShouldReturn404_WhenUserNotFound() {
    AuthRequestDto request = new AuthRequestDto("nonExistent", "password");
    given()
        .contentType(ContentType.JSON)
        .body(request)
        .post("/auth/login")
        .then()
        .statusCode(404);
  }
}
