# ✍️ Personal Blog

[← Back to Roadmap Projects](../README.md)

A full-stack personal blog platform built with Spring Boot, proposed
by [roadmap.sh](https://roadmap.sh/projects/personal-blog).

## 🌟 Highlights

- **SQLite File Storage:** Implemented a basic repository layer using SQLite (previously JSON
  files).
- **Spring Boot & Thymeleaf:** Built a classic server-side rendered application utilizing the power
  of Spring MVC and Thymeleaf templates.
- **Security-First:** Integrated `spring-boot-starter-security` to provide robust, configurable
  authentication for admin endpoints.
- **Responsive UI:** Fully stylized using **Bootstrap 5**, ensuring the blog looks great on both
  mobile and desktop devices.
- **Pagination & Filtering**: Implemented server-side pagination and dynamic filtering (by title and date)
  for articles on both the public home page and admin dashboard.
- **Markdown Rendering**: Support for rich text article content using `commonmark-java`,
  dynamically converting Markdown input into HTML on the server-side.
- **Robust Error Handling:** Centralized application exceptions using `@ControllerAdvice`, providing
  a consistent and user-friendly error experience.
- **Comprehensive Testing:** Achieved high test coverage using JUnit 5, Mockito for service-layer
  testing, and `MockMvc` for robust web-layer integration tests.
- **Containerized:** Full Docker and Docker Compose support for portable, reproducible deployments.

## ℹ️ Project Context

This project served as a deep dive into **Java backend development**. Moving away from CLI tools,
the goal was to build a complete web-based system handling HTTP requests, form validation, and user
sessions. It provided practical experience in bridging the gap between raw data storage (JSON files
initially, now SQLite) and a polished, dynamic user interface.

## 🚀 Usage

### 1. Requirements

- Java 21+
- Docker & Docker Compose (optional)

### 2. Run the Application

```bash
git clone https://github.com/massemiso/roadmap-projects
cd roadmap-projects/personal-blog
./mvnw spring-boot:run
```

The server will start on `http://localhost:8080`.

**Default Admin Credentials:**

- **Username:** `admin`
- **Password:** `admin123`

## 📋 API & Controller Endpoints

| Endpoint        | Method | Role   | Description                    |
| :-------------- | :----- | :----- | :----------------------------- |
| `/home`         | GET    | Public | Displays list of articles      |
| `/article/{id}` | GET    | Public | Displays specific article      |
| `/admin`        | GET    | Admin  | Admin dashboard                |
| `/new`          | GET    | Admin  | Form to add new article        |
| `/article`      | POST   | Admin  | Process form to create article |
| `/edit/{id}`    | GET    | Admin  | Form to edit existing article  |
| `/update/{id}`  | POST   | Admin  | Process form to update article |
| `/delete/{id}`  | GET    | Admin  | Delete an article              |

_Note: All Admin endpoints require authentication. Errors (e.g., 404 Not Found) are handled
via `GlobalExceptionHandler` and rendered through `error.html`._

## 🛠️ Technical Architecture

- **Layered Architecture:**
  - **Controller:** Manages HTTP requests, session logic, and model data.
  - **Service:** Contains business logic and orchestrates data manipulation.
  - **Repository:** Handles filesystem interactions and JSON serialization (via Jackson).
- **Security:** In-memory user management configured via `SecurityConfig`.
- **Validation:** Server-side request validation using `jakarta.validation` annotations on DTOs.

## 🧪 Testing

The project includes a suite of tests to ensure stability and reliability:

- **Unit Tests:** Business logic verification for `Article` entity and `ArticleService`.
- **Web Layer Tests:** Integration tests using `MockMvc` to verify endpoint routing, security, and
  response handling for all CRUD operations.

```bash
./mvnw clean test
```

Building this project helped me understand:

- **Server-Side Rendering (SSR):** Mastering templating engines (Thymeleaf) and model attribute
  binding.
- **The PRG Pattern:** Implementing "Post-Redirect-Get" to avoid duplicate form submissions and
  improve user flow.
- **Spring Security:** Managing authentication chains and securing administrative routes.
- **Concurrency & I/O:** Handling file system operations safely in a multi-threaded web environment.
- **Testing:** Designing testable services and verifying web layers with `MockMvc`.

## 🐳 Containerization

This project includes full support for Docker and Docker Compose:

```bash
# Build and run with Docker Compose
docker-compose up --build
```

_The application will persist the SQLite database in a local `data/` volume._

As this is a foundation project, I plan to extend it with:

- [x] **Database Migration:** Moving from `.json` file storage to an embedded database (like H2 or
      SQLite) for better scalability.
- [x] **Markdown Rendering:** Implementing a Markdown parser (e.g., `commonmark-java`) to allow rich
      text articles.
- [x] **Containerization:** Adding a `Dockerfile` for easy deployment.
- [x] **Enhanced Search:** Implementing a search/filtering feature as the article count grows.

---

[← Back to Roadmap Projects](../README.md)
