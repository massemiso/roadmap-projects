# 📦 Task CLI

Simple Task CLI app proposed by [roadmap.sh](https://roadmap.sh/projects/task-tracker)

## 🌟 Highlights

- Add, remove & update daily tasks interactively in your terminal.
- Mark tasks as todo, in-progress or done.
- List all your tasks, filtered by mark if necessary.
- Persistent data on a JSON file.
- Made in Golang with zero external dependencies.

## ℹ️ Overview

This project was built as a way to learn the basics of Go and as part of the backend roadmap
projects proposed by [roadmap.sh/backend](https://roadmap.sh/projects/task-tracker).
I'm a junior backend developer with no professional experience but with desire to learn the
underlyings of web development and coding on its entirety.

### ✍️ Authors

Made by massemiso
[Github](https://github.com/massemiso)
[Gitlab](https://gitlab.com/massemiso)

## 🚀 Usage

_Extract from [roadmap.sh#Example](https://roadmap.sh/projects/task-tracker)_

```bash
# Adding a new task
task-cli add "Buy groceries"
# Output: Task added (ID: 1)

# Updating and deleting tasks
task-cli update 1 "Buy groceries and cook dinner"
task-cli delete 1

# Marking a task as in progress or done
task-cli mark-in-progress 1
task-cli mark-done 1

# Listing all tasks
task-cli list

# Listing tasks by status
task-cli list done
task-cli list todo
task-cli list in-progress
```

## ⬇️ Getting Started

### Prerequisites

- Git
- Go 1.21+ (utilizes `slices` package)

### Installation & Run

1. Clone the repository:

```bash
git clone https://gitlab.com/roadmapsh_backend/task-cli
cd task-cli
```

2. Build the binary:

```bash
go build -o task-cli
```

3. Run:

```bash
./task-cli [add|update|delete|list|mark-in-progress|mark-done]
```

_Note: On the first run, the application will automatically create a `data.json` file in the current directory to store your tasks._

### 🧪 Running Tests

The project includes unit tests for core logic and integration tests for the repository layer.

```bash
go test ./... -v
```

## 🧠 Key Learnings

Building this project helped me master several core Go concepts:

- **Manual Dependency Injection:** Structuring the application into Repository and Service layers and wiring them manually in `main.go` without a framework.
- **Pointers & Struct Mutation:** Using pointer receivers to safely and explicitly mutate state.
- **Robust Error Handling:** Distinguishing between different failure modes (e.g., missing file vs. corrupted data) and propagating errors to the correct layer.
- **Integration Testing:** Managing temporary files and directories using `t.TempDir()` to ensure isolated and repeatable tests.

## 💭 Feedback and Contributing

Feel free to contact me at <massemiso@proton.me>! Any feedback or tips would be appreciated.
