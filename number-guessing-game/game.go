package main

import (
	"fmt"
	"math/rand/v2"
	"time"
)

type GameSession struct {
	Input        *GameInput
	Difficulty   string
	MaxAttempts  uint8
	SecretNumber uint8
	// Leaderboard  *Leaderboard
}

func NewGameSession(gi *GameInput) *GameSession {
	return &GameSession{
		Input: gi,
	}
}

func (gs *GameSession) ResetRound() {
	gs.Difficulty = ""
	gs.MaxAttempts = 0
	gs.SecretNumber = uint8(rand.UintN(100) + 1)
}

func (gs *GameSession) SelectDifficulty() {
	fmt.Println("Welcome to the Number Guessing Game!")
	fmt.Println("I'm thinking of a number between 1 and 100.")
	fmt.Println("\nPlease select the difficulty level:")
	fmt.Println("1. Easy (10 chances)")
	fmt.Println("2. Medium (5 chances)")
	fmt.Println("3. Hard (3 chances)")

	var choiceStr string
	var attempts uint8
	for {
		choice, err := gs.Input.promptNumber("\nEnter your choice: ", 1, 3)
		if err != nil {
			continue
		}

		switch choice {
		case 1:
			choiceStr = "Easy"
			attempts = 10
		case 2:
			choiceStr = "Medium"
			attempts = 5
		case 3:
			choiceStr = "Hard"
			attempts = 3
		default:
			fmt.Println("Wrong choice!")
			continue
		}

		break
	}

	fmt.Printf("\nGreat! You have selected the %s difficulty level.\n", choiceStr)
	fmt.Printf("You have %d chances to guess the correct number.\n", attempts)
	fmt.Println("Let's start the game!")

	gs.Difficulty = choiceStr
	gs.MaxAttempts = attempts
}

func (gs *GameSession) RunRound() {
	startTime := time.Now()
	fmt.Println()
	for i := range gs.MaxAttempts {
		guess, _ := gs.Input.promptNumber("Enter your guess: ", 1, 100)

		if guess == gs.SecretNumber {
			elapsedTime := time.Since(startTime)
			fmt.Printf("Congratulations! You guessed the correct number in %d attempts and %v.\n",
				i+1, elapsedTime.Round(time.Second))
			return
		}

		if guess > gs.SecretNumber {
			fmt.Printf("Incorrect! The number is less than %d\n\n", guess)
		} else {
			fmt.Printf("Incorrect! The number is greater than %d\n\n", guess)
		}

		if i+1 == gs.MaxAttempts {
			break
		}

		wantClue, err := gs.Input.promptYN("Do you want a clue? (Y/N): ")
		if err != nil {
			fmt.Println(err.Error())
		}

		if wantClue == "Y" {
			minClue, maxClue := calculateClue(i+1, gs.SecretNumber)
			fmt.Printf("Clue: The number is between %d and %d\n", minClue, maxClue)
		}
	}

	elapsedTime := time.Since(startTime)
	fmt.Printf("Oops! after %v you didn't make it, the number was %d!\n", elapsedTime.Round(time.Second), gs.SecretNumber)
}
