# 🌤️ Weather API

[← Back to Roadmap Projects](../README.md)

A weather reporting API platform built with Spring Boot, proposed by [roadmap.sh](https://roadmap.sh/projects/weather-api).

## 🌟 Highlights

- **External API Integration:** Seamlessly integrates with third-party weather services (like Visual Crossing) to provide real-time data.
- **RESTful API:** Clean, intuitive endpoints for fetching current weather and forecasts by city.
- **Caching Strategy:** Implements an in-memory caching mechanism to minimize external API calls and latency.
- **Robust Error Handling:** Global exception handling for external service failures and invalid city inputs.
- **Comprehensive Testing:** Validated via unit and integration tests using JUnit 5 & Mockito.

## ℹ️ Project Context

This project was built to master **external API consumption** and **asynchronous service orchestration** in Java. It served as a sandbox for managing sensitive API keys, handling HTTP client configurations, and ensuring service resilience when external dependencies are slow or unavailable.

## 🚀 Usage

### 1. Requirements

- Java 21+
- Maven
- A valid API Key from a weather provider (e.g., Visual Crossing)

### 2. Run the Application

```bash
# Set your API Key
export VISUALCROSSING_API_KEY=your_api_key_here

# Run the app
./mvnw spring-boot:run
```

## 📋 API Endpoints

| Endpoint            | Method | Arguments                 | Description                          |
| :------------------ | :----- | :------------------------ | :----------------------------------- |
| `/{cityCode}/today` | GET    | 'unit' = metric, us or uk | Returns current weather for the city |
| `/{cityCode}/week`  | GET    | 'unit' = metric, us or uk | Returns 7-day forecast for the city  |

## 🛠️ Technical Architecture

- **Controller:** Maps incoming requests to service calls.
- **Service:** Orchestrates business logic, caching, and external service calls.
- **Client:** A dedicated HTTP client wrapper for the external weather provider.
- **Resilience:** Implements basic retry logic and caching to optimize external service usage.

## 🧪 Testing

- **Service Tests:** Mocking the external HTTP client to verify business logic.
- **Web Layer Tests:** Integration tests using `MockMvc` to verify endpoint response structure and status codes.
- **TODO: API Integration Tests**

```bash
./mvnw test
```

## 🧠 Key Learnings

- **HTTP Client Patterns:** Using `RestClient` to interact with external APIs.
- **Data Mapping:** Transforming complex third-party JSON responses into clean domain DTOs.
- **Service Orchestration:** Handling the lifecycle of external API requests safely.
- **Resilience Patterns:** Implementing caching to prevent rate-limiting and improve speed.

---

[← Back to Roadmap Projects](../README.md)
