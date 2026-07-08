# 📝 Blogging Platform API

[← Back to Roadmap Projects](../README.md)

A scalable, professional-grade REST API for a blogging platform, built with Spring Boot and PostgreSQL.

## 🌟 Highlights

- **Database-First Approach:** Uses **PostgreSQL** for reliable data persistence, managed via Spring Data JPA.
- **Advanced Testing Suite:** Implements a robust testing strategy including unit tests, integration tests with **Testcontainers**, and API contract testing using **REST-Assured**.
- **Code Coverage:** Integrated **JaCoCo** to monitor and enforce high test coverage.
- **Docker-Ready:** Includes a `docker-compose.yml` for instant, isolated development environments.
- **Robust Error Handling:** Custom global exception handling for API reliability.

## ℹ️ Project Context

This project represents my transition from simple file-based storage to a professional, database-backed web API. The goal was to build a production-ready blogging backend that focuses on scalability, reliable testing patterns, and modern industry standards like **Testcontainers** for database integration testing.

## 🚀 Usage

### 1. Requirements

- Docker
- Docker Compose 
- Java 21 (if you want to test locally)

### 2. Run the Application

```bash
# Clone the repository
git clone https://github.com/massemiso/roadmap-projects
cd roadmap-projects/blogging-platform-api

# Spin up the infrastructure and application
docker-compose up --build
```

The API will be available at `http://localhost:8080`.

## 📋 API Endpoints

| Endpoint | Method | Status Codes | Description |
| :--- | :--- | :--- | :--- |
| `/posts` | GET | 200 OK | Retrieve all blog posts |
| `/posts/{id}` | GET | 200 OK, 404 Not Found | Retrieve a specific post |
| `/posts` | POST | 201 Created, 400 Bad Request | Create a new post |
| `/posts/{id}` | PUT | 200 OK, 404 Not Found, 400 Bad Request | Update an existing post |
| `/posts/{id}` | DELETE | 204 No Content, 404 Not Found | Delete a post |

## 🛠️ Technical Architecture

- **Layered Architecture:** Clear separation between `Controller`, `Service`, `Repository`, and `DTO` layers.
- **Data Persistence:** Managed by Spring Data JPA and Hibernate with PostgreSQL.
- **Testing:**
  - **Unit Tests:** Business logic validation (Service/Domain).
  - **Integration Tests:** Database interactions using real **PostgreSQL** instances via **Testcontainers**.
  - **API Tests:** End-to-end endpoint verification using **REST-Assured**.

## 🧪 Testing & Quality

I prioritize high-quality code. This project enforces strict standards:
- **JaCoCo:** Automatically generates coverage reports during the `verify` phase.
- **Testcontainers:** Uses real database environments for integration tests, preventing "it works on my machine" bugs.

```bash
./mvnw verify
```

## 🧠 Key Learnings

- **Testcontainers:** Mastering real database integration testing without polluting local machine environments.
- **REST-Assured:** Writing expressive, behavioral API tests that simulate real client usage.
- **PostgreSQL:** Transitioning from file storage (JSON/SQLite) to a robust, enterprise-grade relational database.
- **JaCoCo Reporting:** Using static analysis tools to maintain a high bar for code reliability.

---

[← Back to Roadmap Projects](../README.md)
