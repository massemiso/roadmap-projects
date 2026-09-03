# 📈 GitHub Trending CLI

[← Back to Roadmap Projects](../README.md)

A high-performance CLI tool to fetch and display trending GitHub repositories, built with Go.

## 🌟 Highlights

- **Live Trending Data:** Fetches the latest trending repositories directly from GitHub.
- **Customizable Filtering:** Supports filtering by time range (`day`, `week`, `month`, `year`) and result limits.
- **Clean CLI UX:** User-friendly output with color-coded information. Supports `NO_COLOR` environment variable for accessibility and plain-text output.
- **Data Export:** Supports exporting trending data to `CSV` or `JSON` files.
- **Caching:** Automatically caches API responses for 5 minutes to reduce unnecessary network calls and improve performance.
- **Robust Error Handling:** Handles API errors and invalid user inputs gracefully.
- **Pure Go:** Built using Go Modules with efficient standard library usage.

## ℹ️ Project Context

This project was built to master **Go CLI development** and **REST API integration**. The goal was to create a reliable CLI tool that fetches real-time data, parses JSON responses, and presents them in a clean, readable terminal format. It served as a practice ground for command-line argument parsing and HTTP client implementations.

## 🚀 Usage

### 1. Requirements

- Go 1.20+

### 2. Run the Application

```bash
# Clone the repository
git clone https://github.com/massemiso/roadmap-projects
cd roadmap-projects/github-trending

# Build the tool
go build -o trending-repos

# Run with custom duration and limit (or no flags if you want default)
./trending-repos --duration month --limit 20
```

## 📋 Example Usage

```bash
# Get trending repos for the current week (default)
./trending-repos

# Get trending repos for the year
./trending-repos --duration year --limit 5

# Get trending repos for the year (no color in output)
NO_COLOR=1 ./trending-repos --duration year --limit 5

# Export trending repos to JSON
./trending-repos --export json

# Export trending repos to CSV
./trending-repos --export csv
```

## 🛠️ Technical Architecture

- **`cmd/`**: Manages the CLI command logic and flag parsing.
- **`internal/github/`**: Handles the GitHub API integration and model data structures.
- **`internal/ui/`**: Manages terminal formatting, coloring, and user output.
- **Data Parsing:** Direct JSON deserialization from the GitHub REST API.

## 🧪 Testing

- **Unit Tests:** Logic validation for input parsing and API data processing.

```bash
go test ./... -v
```

## 🧠 Key Learnings

- **Go CLI Patterns:** Mastering `flag` packages and sub-command architectures.
- **API Integration:** Consuming public REST APIs with Go's `net/http`.
- **JSON Parsing:** Using `encoding/json` to map complex API payloads to Go structs.
- **UI in Terminal:** Understanding ANSI colors and formatting for a better user experience.

## 📋 TODO & Future Improvements

- [x] **Data Export:** Add flags to export trending data to `CSV` or `JSON` files.
- [x] **Caching:** Implement a local cache (e.g., in `/tmp`) to store API responses for 5 minutes, reducing network calls.

---

[← Back to Roadmap Projects](../README.md)
