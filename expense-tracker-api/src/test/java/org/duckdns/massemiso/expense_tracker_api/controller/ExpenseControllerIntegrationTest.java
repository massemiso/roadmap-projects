package org.duckdns.massemiso.expense_tracker_api.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import org.duckdns.massemiso.expense_tracker_api.ExpenseTrackerApiApplication;
import org.duckdns.massemiso.expense_tracker_api.TestcontainersConfiguration;
import org.duckdns.massemiso.expense_tracker_api.dto.AuthRequestDto;
import org.duckdns.massemiso.expense_tracker_api.dto.AuthResponseDto;
import org.duckdns.massemiso.expense_tracker_api.dto.ExpenseRequestDto;
import org.duckdns.massemiso.expense_tracker_api.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(classes = ExpenseTrackerApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExpenseControllerIntegrationTest {

  @LocalServerPort
  private Integer port;

  private String token;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
    
    String username = "user" + System.currentTimeMillis();
    AuthRequestDto authRequest = new AuthRequestDto(username, "password123");
    
    // Register
    given()
        .contentType(ContentType.JSON)
        .body(authRequest)
        .post("/auth/register")
        .then()
        .statusCode(201);

    // Login
    token = given()
        .contentType(ContentType.JSON)
        .body(authRequest)
        .post("/auth/login")
        .then()
        .statusCode(200)
        .extract()
        .as(AuthResponseDto.class)
        .token();
  }

  @Test
  void save_ShouldReturn201() {
    ExpenseRequestDto request = new ExpenseRequestDto("Coffee", Category.LEISURE, LocalDate.now());

    given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body(request)
        .post("/api/expenses")
        .then()
        .statusCode(201)
        .body("id", notNullValue())
        .body("description", equalTo("Coffee"));
  }

  @Test
  void save_ShouldReturn400_WhenInvalidData() {
    ExpenseRequestDto request = new ExpenseRequestDto("", null, null); // Invalid

    given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body(request)
        .post("/api/expenses")
        .then()
        .statusCode(400);
  }

  @Test
  void getById_ShouldReturn401_WhenNoAuth() {
    given()
        .get("/api/expenses/1")
        .then()
        .statusCode(401);
  }

  @Test
  void getAll_ShouldReturn200() {
    given()
        .header("Authorization", "Bearer " + token)
        .get("/api/expenses")
        .then()
        .statusCode(200);
  }

  @Test
  void getById_ShouldReturn200_WhenAuthorized() {
    // 1. Create an expense first
    ExpenseRequestDto request = new ExpenseRequestDto("Lunch", Category.LEISURE, LocalDate.now());
    Long id = given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body(request)
        .post("/api/expenses")
        .then()
        .statusCode(201)
        .extract()
        .jsonPath()
        .getLong("id");

    // 2. Get the expense
    given()
        .header("Authorization", "Bearer " + token)
        .get("/api/expenses/" + id)
        .then()
        .statusCode(200)
        .body("id", equalTo(id.intValue()))
        .body("description", equalTo("Lunch"));
  }

  @Test
  void getById_ShouldReturn404_WhenExpenseNotFound() {
    given()
        .header("Authorization", "Bearer " + token)
        .get("/api/expenses/9999")
        .then()
        .statusCode(404);
  }

  @Test
  void update_ShouldReturn200_WhenAuthorized() {
    // 1. Create
    ExpenseRequestDto request = new ExpenseRequestDto("Old", Category.UTILITIES, LocalDate.now());
    Long id = given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body(request)
        .post("/api/expenses")
        .then()
        .statusCode(201)
        .extract()
        .jsonPath()
        .getLong("id");

    // 2. Update
    ExpenseRequestDto updateRequest = new ExpenseRequestDto("New", Category.UTILITIES, LocalDate.now());
    given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body(updateRequest)
        .put("/api/expenses/" + id)
        .then()
        .statusCode(200)
        .body("description", equalTo("New"));
  }

  @Test
  void update_ShouldReturn404_WhenExpenseNotFound() {
    ExpenseRequestDto request = new ExpenseRequestDto("New", Category.UTILITIES, LocalDate.now());
    given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body(request)
        .put("/api/expenses/9999")
        .then()
        .statusCode(404);
  }

  @Test
  void update_ShouldReturn400_WhenInvalidData() {
    ExpenseRequestDto request = new ExpenseRequestDto("", null, null); // Invalid
    given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body(request)
        .put("/api/expenses/1")
        .then()
        .statusCode(400);
  }

  @Test
  void delete_ShouldReturn204_WhenAuthorized() {
    // 1. Create
    ExpenseRequestDto request = new ExpenseRequestDto("Delete Me", Category.CLOTHING, LocalDate.now());
    Long id = given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body(request)
        .post("/api/expenses")
        .then()
        .statusCode(201)
        .extract()
        .jsonPath()
        .getLong("id");

    // 2. Delete
    given()
        .header("Authorization", "Bearer " + token)
        .delete("/api/expenses/" + id)
        .then()
        .statusCode(204);
  }

  @Test
  void delete_ShouldReturn404_WhenExpenseNotFound() {
    given()
        .header("Authorization", "Bearer " + token)
        .delete("/api/expenses/9999")
        .then()
        .statusCode(404);
  }
}
