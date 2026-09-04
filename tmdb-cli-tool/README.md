# TMDB CLI Tool

[← Back to Roadmap Projects](../README.md)

A CLI application to interact with The Movie Database (TMDB) API, allowing users to fetch and display movies in various categories (popular, trending, top-rated, upcoming) directly from their terminal.

## 🚀 Features

- **Language:** Go
- **API Integration:** Consumes the [TMDB REST API](https://developer.themoviedb.org/).
- **Categories:** Supports fetching `playing` (now playing), `popular`, `top` (top-rated), and `upcoming` movies.
- **Customization:**
  - **Language:** Specify output language (e.g., `en`, `es`, `fr`, `zh`, etc.).
  - **Output Format:** Choose between a tabular view (default) or a verbose text view.
  - **Data Export:** Export fetched data to `JSON` or `CSV` files.
- **Caching:** Local caching to reduce unnecessary API requests.
- **Robustness:** Includes error handling for API failures, status code checks, and input validation.
- **Clean CLI UX:** User-friendly output with color-coded information. Supports `NO_COLOR` environment variable for accessibility and plain-text output.

## 🛠 Prerequisites

- Go (1.20+)
- A valid TMDB API Read Access Token. You can generate one from your [TMDB Account Settings](https://www.themoviedb.org/settings/api).

## 📥 Installation

1. Clone the repository:

```bash
git clone https://github.com/massemiso/roadmap-projects
cd roadmap-projects/tmdb-cli-tool
```

2. Build the project:

```bash
go build -o tmdb-cli main.go
```

## ⚙️ Configuration

The tool requires a `TMDB_API_KEY` environment variable. You can set it in your environment or use a `.env` file in the project root:

```bash
export TMDB_API_KEY="your_api_read_access_token_here"
```

## 🏃 Usage

```bash
# Basic usage (defaults to English tabular output)
TMDB_API_KEY="your_key" ./tmdb-cli --type popular

# Using specific language and output format
TMDB_API_KEY="your_key" ./tmdb-cli --type top --lang es --text

# Using specific language, output format, and exporting data
TMDB_API_KEY="your_key" ./tmdb-cli --type top --lang es --text --export csv
```

### Options

| Flag       | Description                                                  | Default |
| :--------- | :----------------------------------------------------------- | :------ |
| `--type`   | Category of movies (`playing`, `popular`, `top`, `upcoming`) | N/A     |
| `--lang`   | ISO language code (`en`, `es`, `fr`, `de`, etc.)             | `en`    |
| `--text`   | Print output as formatted text instead of a table            | `false` |
| `--export` | Export data to file (`json`, `csv`, `none`)                  | `none`  |

## 🧪 Testing

The test suite requires the `TMDB_API_KEY` to be set in the environment:

```bash
TMDB_API_KEY="test" go test -v ./...
```

## 📋 TODO & Future Improvements

- [x] **Data Export:** Add flags to export trending data to `CSV` or `JSON` files.
- [x] **Caching:** Implement a local cache (e.g., in `/tmp`) to store API responses for 5 minutes, reducing network calls.

---

[← Back to Roadmap Projects](../README.md)
