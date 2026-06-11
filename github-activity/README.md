# 📦 Github Activity

[← Back to Roadmap Projects](../README.md)

Simple Github User Activity CLI app proposed by [roadmap.sh](https://roadmap.sh/projects/github-user-activity)

## 🌟 Highlights

- **Live Activity Tracking:** See the latest activity of any GitHub user directly in your terminal.
- **Customizable Depth:** Limit the number of events displayed using the `--limit` flag.
- **Terminal Friendly:** Supports `NO_COLOR` environment variable for accessibility and script compatibility.
- **Rich Event Detail:** Dynamically parses payloads to show specific information like commit messages, branch names, and issue titles.

## ℹ️ Project Context

This project was built to master the fundamentals of **Go's standard library**, specifically focusing on how to build robust CLI tools that interact with external REST APIs.

The core challenge was handling the "polymorphic" nature of the GitHub Events API—where the data structure changes significantly depending on the event type—and presenting that data in a clean, human-readable format without using external frameworks.

## 🚀 Usage

```bash
# Basic usage
user% ./github-activity massemiso
Recent activity of massemiso
+ (2026-06-11 19:19:45) Pushed to main at massemiso/roadmap-projects
+ (2026-06-11 16:21:56) Pushed to main at massemiso/roadmap-projects
+ (2026-06-11 00:12:22) Pushed to main at massemiso/roadmap-projects
+ (2026-06-10 00:24:25) Pushed to main at massemiso/roadmap-projects
+ (2026-06-10 00:20:30) Created a branch/tag on massemiso/roadmap-projects
	* Multiple projects proposed in roadmap.sh
+ (2026-06-09 19:54:04) Starred moverest/sway-resize
+ (2026-05-18 15:52:37) Pushed to main at massemiso/supermarket-api
+ (2026-05-18 15:49:44) Pushed to main at massemiso/supermarket-api
+ (2026-05-16 19:15:37) Pushed to main at massemiso/supermarket-api
+ (2026-05-16 19:12:44) Pushed to main at massemiso/supermarket-api
+ (2026-05-16 18:55:08) Pushed to main at massemiso/supermarket-api
+ (2026-05-15 14:43:52) Pushed to main at massemiso/supermarket-api
+ (2026-05-14 20:41:20) Pushed to main at massemiso/supermarket-api
+ (2026-05-14 15:45:23) Pushed to main at massemiso/supermarket-api
+ (2026-05-13 19:57:48) Pushed to main at massemiso/supermarket-api
+ (2026-05-13 13:04:22) Pushed to main at massemiso/supermarket-api

# Limit results to the last 15 events
user% ./github-activity --limit=15 sindresorhus
Recent activity of sindresorhus
+ (2026-06-11 20:50:50) Deleted branch 'ss/no-error-property-assignment' on sindresorhus/eslint-plugin-unicorn
+ (2026-06-11 20:50:49) Pushed to main at sindresorhus/eslint-plugin-unicorn
+ (2026-06-11 20:50:49) Closed issue #1883 ('Rule proposal: `no-error-prototype-property-assign`') on sindresorhus/eslint-plugin-unicorn
+ (2026-06-11 20:50:48) Merged a pull request (3114) on sindresorhus/eslint-plugin-unicorn
+ (2026-06-11 20:49:37) Deleted branch 'ss/consistent-destructuring-getters' on sindresorhus/eslint-plugin-unicorn
+ (2026-06-11 20:49:34) Merged a pull request (3115) on sindresorhus/eslint-plugin-unicorn
+ (2026-06-11 20:49:36) Closed issue #2469 ('Make `consistent-destructuring` configurable') on sindresorhus/eslint-plugin-unicorn
+ (2026-06-11 20:49:35) Pushed to main at sindresorhus/eslint-plugin-unicorn
+ (2026-06-11 20:44:19) Opened a pull request (3116) on sindresorhus/eslint-plugin-unicorn
+ (2026-06-11 20:44:06) Created a branch/tag on sindresorhus/eslint-plugin-unicorn
	* More than 100 powerful ESLint rules
+ (2026-06-11 20:41:17) Pushed to ss/no-unreadable at sindresorhus/eslint-plugin-unicorn
+ (2026-06-11 20:36:50) Pushed to ss/no-error-property-assignment at sindresorhus/eslint-plugin-unicorn
+ (2026-06-11 20:36:47) Pushed to ss/consistent-destructuring-getters at sindresorhus/eslint-plugin-unicorn
+ (2026-06-11 20:35:30) Merged a pull request (3112) on sindresorhus/eslint-plugin-unicorn
+ (2026-06-11 20:35:33) Deleted branch 'ss/do-this-httpsgithubcomsindresorhuseslintpluginunic2' on sindresorhus/eslint-plugin-unicorn

# Disable colors for logging or plain-text environments
user% NO_COLOR=true ./github-activity massemiso
```

### ✍️ Author

Made by **massemiso**
[Github](https://github.com/massemiso) | [Email](mailto:massemiso@proton.me)

## ⬇️ Getting Started

### Prerequisites

- Git
- Go 1.20+ (supports `time.DateTime` and `json.RawMessage`)

### Installation & Run

1. Clone the repository (Sparse checkout for this project only):

```bash
git clone --filter=blob:none --sparse https://github.com/massemiso/roadmap-projects
cd roadmap-projects
git sparse-checkout set github-activity
cd github-activity
```

2. Build the binary:

```bash
go build -o github-activity
```

_This will build the binary in the current directory_

3. Run:

```bash
./github-activity [--limit=n] <username>
```

## 🛠️ Technical Architecture

This application follows a modular design focused on **Separation of Concerns**:

- **Polymorphic Payloads:** Uses `json.RawMessage` to defer parsing of GitHub's dynamic event payloads.
- **Table-Driven Formatters:** Events are mapped to specific formatter functions, making the code highly extensible for new GitHub features.
- **Dependency Injection:** The `run` logic is decoupled from the HTTP service via a `GitHubServiceInterface`, allowing for robust testing.
- **Clean UI Layer:** A dedicated `View` struct manages terminal output standards.

## 🧪 Running Tests

The project features a comprehensive test suite covering all layers:

- **Unit Tests:** Event formatting and string manipulation logic.
- **Mock Tests:** Full application workflow simulation using service mocks.
- **Integration Tests:** Local API server simulation using `net/http/httptest`.

```bash
go test ./... -v
```

## 🧠 Key Learnings

- **Working with External APIs:** Navigating REST documentation and handling real-world HTTP status codes.
- **Go Interfaces:** Using interfaces to write "implementation-blind" code that is easy to mock and test.
- **Deferred JSON Parsing:** Mastering `RawMessage` to handle complex, nested, and changing JSON shapes.
- **CLI Standards:** Implementing industry-standard behavior like flag parsing and `NO_COLOR` support.

## 📋 TODO

- [ ] **Caching:** Implement a local cache (e.g., in `/tmp`) to store API responses for 5 minutes, reducing network calls and avoiding rate limits.

---

[← Back to Roadmap Projects](../README.md)
