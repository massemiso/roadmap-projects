# 📦 Task CLI

[← Back to Roadmap Projects](../README.md)

Simple Task CLI app proposed by [roadmap.sh](https://roadmap.sh/projects/task-tracker)

## 🌟 Highlights

- **Task Management:** Add, remove, and update daily tasks interactively in your terminal.
- **Status Tracking:** Mark tasks as `todo`, `in-progress`, or `done`.
- **Filtered Listing:** List all your tasks or filter them by their current status.
- **Persistence:** All data is safely stored in a local JSON file.
- **Pure Go:** Built using only the Go Standard Library with zero external dependencies.

## ℹ️ Project Context

This project was built to master **File System Management** and **JSON Serialization** in Go.

The goal was to create a reliable CLI tool that manages state locally on the user's machine. It served as a foundational project to understand how to design data models, handle pointer-based state mutation, and ensure data integrity across multiple application runs.

## 🚀 Usage

```bash
# Adding a new task
./task-cli add "Buy groceries"

# Updating and deleting tasks
./task-cli update 1 "Buy groceries and cook dinner"
./task-cli delete 1

# Marking status
./task-cli mark-in-progress 1
./task-cli mark-done 1

# Listing tasks (all or filtered)
./task-cli list
./task-cli list done
./task-cli list todo
```

### ✍️ Author

Made by **massemiso**
[Github](https://github.com/massemiso) | [Email](mailto:massemiso@proton.me)

## ⬇️ Getting Started

### Prerequisites

- Git
- Go 1.21+ (utilizes the `slices` package)

### Installation & Run

1. Clone the repository (Sparse checkout for this project only):

```bash
git clone --filter=blob:none --sparse https://github.com/massemiso/roadmap-projects
cd roadmap-projects
git sparse-checkout set task-cli
cd task-cli
```

2. Build the binary:

```bash
go build -o task-cli
```

3. Run:

```bash
./task-cli [command] [arguments]
```

_Note: On the first run, the application will automatically create a `data.json` file to store your tasks._

## 🛠️ Technical Architecture

- **Separation of Concerns:** Uses a clean division between the Repository (storage) and Service (logic) layers.
- **JSON Persistence:** Implements manual encoding/decoding for local storage.
- **Pointer Mutation:** Leverages Go pointers to modify task states explicitly and efficiently.
- **Input Validation:** Robust checking of IDs and status strings to prevent data corruption.

## 🧪 Running Tests

The project includes a suite of tests to verify task operations and file handling:

- **Unit Tests:** Core logic for task management and status transitions.
- **Integration Tests:** Repository layer tests using `t.TempDir()` for isolated file system testing.

```bash
go test ./... -v
```

## 🧠 Key Learnings

Building this project helped me understand:

- **Local State Management:** Handling persistent storage without a database.
- **Pointers & Struct Mutation:** Using pointer receivers to safely mutate internal state.
- **Go Standard Library:** Leveraging `encoding/json` and `os` packages for low-level system tasks.
- **Error Propagation:** Designing error flows that provide clear feedback to the CLI user.

---

[← Back to Roadmap Projects](../README.md)
