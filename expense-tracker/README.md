# 📦 Expense Tracker

[← Back to Roadmap Projects](../README.md)

Simple Expense Tracker CLI app proposed by [roadmap.sh](https://roadmap.sh/projects/expense-tracker)

## 🌟 Highlights

- **Full Expense CRUD:** Add, update, delete, and list expenses with descriptions, amounts, and categories.
- **Financial Summaries:** View total expenses or filter summaries by a specific month.
- **Monthly Budgets:** Set spending limits per month and receive warnings when you exceed them.
- **Category Filtering:** List expenses filtered by custom categories.
- **CSV Export:** Export all expenses to a CSV file for external use.
- **Terminal Friendly:** Supports `NO_COLOR` environment variable for accessibility and script compatibility.
- **Pure Go:** Built using only the Go Standard Library with zero external dependencies.

## ℹ️ Project Context

This project was built to master **CLI Architecture**, **Dependency Injection**, and **Layered Application Design** in Go.

The goal was to create a feature-rich CLI tool that manages financial data locally, going beyond simple CRUD to implement budgeting logic, CSV exporting, and multi-format data persistence. It served as a project to understand how to design testable applications using interfaces, decouple I/O from business logic, and build a robust command dispatcher.

## 🚀 Usage

```bash
# Adding expenses
./expense-tracker add --description="Lunch" --amount=20
# Expense added successfully (ID: 1)

./expense-tracker add --description="Dinner" --amount=10 --category="Food"
# Expense added successfully (ID: 2)

# Updating an expense
./expense-tracker update --id=1 --description="Business Lunch" --amount=25.00

# Deleting an expense
./expense-tracker delete --id=2

# Listing all expenses
./expense-tracker list
# |ID |Date        |Description |Amount   |Category    |
# |1  |2026-06-16  |Business L..|$25.00   |            |

# Listing expenses filtered by category
./expense-tracker list --category="Food"

# Viewing total summary
./expense-tracker summary
# Total expenses: $25.00

# Viewing summary for a specific month
./expense-tracker summary --month=6
# Total expenses for June: $25.00

# Setting a monthly budget
./expense-tracker budget --month=6 --amount=500.0
# Set budget $500.00 for June successfully!

# Exporting expenses to CSV
./expense-tracker export
# Expenses exported to 'expenses.csv' successfully

# Clearing all stored expenses
./expense-tracker clean
# Expenses cleared successfully
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
git sparse-checkout set expense-tracker
cd expense-tracker
```

2. Build the binary:

```bash
go build -o expense-tracker
```

3. Run:

```bash
./expense-tracker [command] [--flags]
```

_Note: On the first run, the application will automatically create an `expenses.json` file in your system's config directory to store your data._

## 🛠️ Technical Architecture

This application follows a modular design focused on **Separation of Concerns**:

- **Layered Architecture:** Clean division between the CLI layer (`main.go`), Service layer (`service.go`), Data layer (`data.go`), and Domain models (`expense.go`).
- **Dependency Injection:** The `AppEnv` struct injects an `ExpenseServiceInterface`, decoupling all business logic from the CLI dispatcher for robust testing.
- **Interface-Driven Design:** Both the Service and Data layers are defined by interfaces (`ExpenseServiceInterface`, `ExpenseDataInterface`), enabling easy mocking at every boundary.
- **Budget Engine:** A post-operation check automatically compares monthly spending against user-defined budgets, returning warning errors without blocking the operation.
- **Command Dispatch Map:** A `map[string]func` routes subcommands to handler methods, keeping the dispatcher clean and extensible.

## 🧪 Running Tests

The project features a comprehensive test suite covering all layers:

- **Unit Tests:** Domain model operations like summaries, deletion, and string formatting.
- **Integration Tests:** Service layer tests using `t.TempDir()` for isolated file system testing.
- **Mock Tests:** CLI handler tests using a mock service to verify flag parsing, validation, output formatting, and error propagation.

```bash
go test ./... -v
```

## 🧠 Key Learnings

Building this project helped me understand:

- **Dependency Injection in Go:** Using interfaces and struct injection to build a fully testable CLI application without external frameworks.
- **Layered Testing Strategies:** Writing unit, integration, and mock-based tests at different abstraction levels to achieve high coverage.
- **CLI Design Patterns:** Building a dispatcher with `flag.NewFlagSet` for subcommand parsing and using `flag.Visit` for fine-grained flag detection.
- **Error as Warning Pattern:** Returning non-nil errors alongside valid data to signal warnings (e.g., budget exceeded) without blocking operations.
- **Go Standard Library:** Leveraging `encoding/json`, `encoding/csv`, `os`, and `flag` packages for a full-featured application with zero dependencies.

---

[← Back to Roadmap Projects](../README.md)
