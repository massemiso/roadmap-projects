# ✅ Todo List API

[← Back to Roadmap Projects](../README.md)

A secure, multi-user Todo List REST API built with Spring Boot, PostgreSQL, and JWT Authentication.

## 🌟 Highlights

- **User Authentication:** Secure JWT-based authentication for private user data.
- **Dynamic Filtering:** Advanced search/filtering for todos (title, status, etc.) using JPA Specifications.
- **Database Persistence:** Reliable storage with PostgreSQL.
- **Robust Security:** Implements Spring Security with custom JWT filters.
- **Containerized:** Full Docker support for portable development.

## ℹ️ Project Context

This project focuses on building a **production-ready REST API** with real-world authentication requirements. I aimed to master user management, JWT token handling, and dynamic database querying using JPA Specifications, moving from simple CRUD to a secure, multi-tenant-style architecture.

## 🚀 Usage

### 1. Requirements

- Docker
- Docker Compose 
- Java 21

### 2. Run the Application

```bash
# Clone the repository
git clone https://github.com/massemiso/roadmap-projects
cd roadmap-projects/todo-list-api

# Spin up the infrastructure and application
docker-compose up --build
```

The API will be available at `http://localhost:8080`.

## 📋 API Endpoints

| Endpoint | Method | Status Codes | Description |
| :--- | :--- | :--- | :--- |
| `/register` | POST | 201, 400 | Register a new user |
| `/login` | POST | 200, 401 | Authenticate and get JWT |
| `/todos` | GET | 200 | Retrieve all todos (filtered) |
| `/todos` | POST | 201, 400 | Create a new todo |
| `/todos/{id}` | PUT | 200, 404 | Update todo status/content |
| `/todos/{id}` | DELETE | 204, 404 | Delete a todo |

## 🛠️ Technical Architecture

- **Security:** JWT (JSON Web Token) based authentication with stateless session management.
- **Dynamic Querying:** JPA Specifications used for complex filtering in the `Todo` list.
- **Data Persistence:** Spring Data JPA with PostgreSQL.
- **Layered Design:** Strict separation of DTOs, Entities, Services, and Controllers.

## 🧪 Testing & Quality

- **Unit Testing:** Comprehensive coverage of authentication logic and service layers.
- **Integration Testing:** Verification of database interactions and endpoint security.

```bash
./mvnw verify
```

## 🧠 Key Learnings

- **JWT Auth:** Implementing custom filters and token providers in Spring Security.
- **JPA Specifications:** Building flexible query dynamic filters.
- **Exception Strategy:** Custom `GlobalErrorHandler` for cleaner API responses.

## 🚧 Future Improvements

- [ ] **Rate Limiting & Throttling:** Implementing request limiting to protect API stability.
- [ ] **Refresh Token Mechanism:** Adding secure token rotation for better UX and security.

---

[← Back to Roadmap Projects](../README.md)
