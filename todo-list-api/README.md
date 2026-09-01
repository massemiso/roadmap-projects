# ✅ Todo List API

[← Back to Roadmap Projects](../README.md)

A secure, multi-user Todo List REST API built with Spring Boot, PostgreSQL, and JWT Authentication.

## 🌟 Highlights

- **User Authentication:** Secure JWT-based authentication for private user data.
- **Dynamic Filtering:** Advanced search/filtering for todos (title, status, etc.) using JPA Specifications.
- **Database Persistence:** Reliable storage with PostgreSQL.
- **Rate Limiting & Throttling:** Implemented request limiting to protect API stability.
- **Refresh Token Mechanism:** Added secure token rotation for better UX and security.
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

| Endpoint         | Method | Status Codes | Description                       |
| :--------------- | :----- | :----------- | :-------------------------------- |
| `/auth/register` | POST   | 201, 400     | Register a new user               |
| `/auth/login`    | POST   | 200, 401     | Authenticate and get JWT          |
| `/auth/refresh`  | POST   | 200, 401     | Get a new JWT given refresh token |
| `/todos`         | GET    | 200          | Retrieve all todos (filtered)     |
| `/todos`         | POST   | 201, 400     | Create a new todo                 |
| `/todos/{id}`    | PUT    | 200, 404     | Update todo status/content        |
| `/todos/{id}`    | DELETE | 204, 404     | Delete a todo                     |

### 📚 API Examples

#### 1. Authentication

**Register:**

```http
POST /auth/register
Content-Type: application/json

{
  "name": "user1",
  "email": "user@email.com"
  "password": "password123"
}
```

**Login:**

```http
POST /auth/login
Content-Type: application/json

{
  "email": "user@email.com"
  "password": "password123"
}
```

**Refresh Token:**

```http
POST /auth/refresh
Authorization: Bearer <refresh_token>
```

#### 2. Todo Operations

**Create Todo:**

```http
POST /todos
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "title": "Buy groceries",
  "description": "Milk, eggs, bread"
}
```

**Updat Todo:**

```http
PUT /todos/{id}
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "title": "Updated groceries",
  "description": "Milk, eggs, bread 2",
  "completed": true
}
```

**Get Todos (with filtering):**

```http
# Filter by status
GET /todos?completed=false
Authorization: Bearer <access_token>

# Filter by title keyword
GET /todos?title=groceries
Authorization: Bearer <access_token>
```

## 🛠️ Technical Architecture

- **Security:** JWT (JSON Web Token) based authentication with stateless session management, including refresh tokens and rate limiting.
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

- **JWT Auth & Refresh Tokens:** Implementing custom filters, token providers, and secure rotation for extended sessions.
- **Rate Limiting:** Managing request throughput to ensure API stability.
- **JPA Specifications:** Building flexible query dynamic filters.
- **Exception Strategy:** Custom `GlobalErrorHandler` for cleaner API responses.

## 🚧 Future Improvements

- [x] **Enhanced Search:** Implementing a global search feature for todos.

---

[← Back to Roadmap Projects](../README.md)
