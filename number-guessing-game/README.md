# 🎲 Number Guessing Game

[← Back to Roadmap Projects](../README.md)

A classic number guessing game CLI app proposed by [roadmap.sh](https://roadmap.sh/projects/number-guessing-game)

## 🌟 Highlights

- **Strategy-Based Hint System:** Uses a higher-order function pattern to dynamically select hints (Range, Parity, Digits), making the game feel intelligent and varied.
- **Leaderboard Persistence:** Tracks high scores across difficulty levels (Easy, Medium, Hard) using JSON-based storage.
- **Robust CLI Interaction:** Handles cross-platform terminal clearing, `NO_COLOR` environment support, and rigorous input sanitization.
- **Testable Architecture:** Built with dependency injection for I/O, allowing for seamless testing of complex business logic.
- **Pure Go:** Built using only the Go Standard Library with zero external dependencies.

## ℹ️ Project Context

This project was built to deepen my understanding of **CLI Architecture**, **Strategy Patterns**, and **Unit Testing** in Go.

The goal was to build a clean, modular application where I could experiment with higher-order functions (using Go functions as values) to create a hint system. It served as a sandbox for implementing persistent state (leaderboards) and writing table-driven tests for complex mathematical and logic-based features.

## 🚀 Usage

```bash
# Running the game
go run .

# The game will prompt for:
# 1. Difficulty Level (Easy, Medium, Hard)
# 2. Guesses
# 3. Hint requests

# Leaderboard is automatically loaded/saved as 'leaderboard.json'
```

### ✍️ Author

Made by **massemiso**
[Github](https://github.com/massemiso) | [Email](mailto:massemiso@proton.me)

## ⬇️ Getting Started

### Prerequisites

- Git
- Go 1.22+ (utilizes `math/rand/v2`)

### Installation & Run

1. Clone the repository:

```bash
git clone https://github.com/massemiso/roadmap-projects
cd roadmap-projects/number-guessing-game
```

2. Build the binary:

```bash
go build -o number-guessing-game
```

3. Run:

```bash
./number-guessing-game
```

## 🛠️ Technical Architecture

This application follows a modular design focused on **Separation of Concerns**:

- **Layered Architecture:** Clear distinction between the CLI layer (`main.go`), Service layer (`game.go`), and Persistence layer (`leaderboard.go`).
- **Strategy Pattern:** Hint generation is implemented as a slice of `HintFunc` (higher-order functions), allowing the system to pick random strategies at runtime without tight coupling.
- **Dependency Injection:** The `GameSession` accepts `GameInput` and `GameOutput` dependencies, making the entire game loop testable without needing actual user input.
- **Robust Testing:** Logic is decoupled into testable chunks, allowing for extensive table-driven tests for both clues and scoring logic.

## 🧪 Running Tests

The project features a comprehensive test suite covering the core logic:

- **Logic Tests:** Table-driven tests for hint generation and leaderboard scoring logic.
- **Mocking:** Strategy functions are tested in isolation.

```bash
go test ./... -v
```

## 🧠 Key Learnings

Building this project helped me understand:

- **Higher-Order Functions in Go:** Using function types (`HintFunc`) as first-class citizens to implement flexible strategy patterns.
- **Table-Driven Testing:** The idiomatic Go approach to testing, making it easy to add edge cases for complex math and logic.
- **State Management:** Handling simple persistence with JSON and basic file I/O while keeping the code clean.
- **Game Loop Design:** Implementing a clean CLI game loop with exit strategies, difficulty settings, and persistent score tracking.
- **Go Standard Library:** Leveraging `math/rand/v2` and `encoding/json` for efficient and clean implementation.

---

[← Back to Roadmap Projects](../README.md)
