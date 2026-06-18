package main

import (
	"math/rand/v2"
	"time"
)

type GameSession struct {
	Input        *GameInput
	Output       *GameOutput
	Difficulty   string
	MaxAttempts  uint8
	SecretNumber uint8
	// Leaderboard  *Leaderboard
}

func NewGameSession(in *GameInput, out *GameOutput) *GameSession {
	return &GameSession{
		Input:  in,
		Output: out,
	}
}

func (gs *GameSession) ResetRound() {
	gs.Difficulty = ""
	gs.MaxAttempts = 0
	gs.SecretNumber = uint8(rand.UintN(100) + 1)
}

func (gs *GameSession) SelectDifficulty() {
	gs.Output.PrintInfo("Welcome to the Number Guessing Game!\n")
	gs.Output.Println("I'm thinking of a number between 1 and 100.")
	gs.Output.Println("Please select the difficulty level:")
	gs.Output.Println("1. Easy (10 chances)")
	gs.Output.Println("2. Medium (5 chances)")
	gs.Output.Println("3. Hard (3 chances)")

	for {
		choice := gs.Input.promptNumber("\nEnter your choice: ", 1, 3)
		switch choice {
		case 1:
			gs.Difficulty, gs.MaxAttempts = "Easy", 10
		case 2:
			gs.Difficulty, gs.MaxAttempts = "Medium", 5
		case 3:
			gs.Difficulty, gs.MaxAttempts = "Hard", 3
		default:
			gs.Output.PrintError("Wrong choice!")
			continue
		}
		break
	}

	gs.Output.PrintDifficulty("\nGreat! You have selected the %s difficulty level.\n", gs.Difficulty)
	gs.Output.Printf("You have %d chances to guess the correct number.\n", gs.MaxAttempts)
	gs.Output.Println("Let's start the game!")
}

func (gs *GameSession) RunRound() {
	startTime := time.Now()
	gs.Output.Println("")

	for i := range gs.MaxAttempts {
		guess := gs.Input.promptNumber("Enter your guess: ", 1, 100)

		if guess == gs.SecretNumber {
			elapsedTime := time.Since(startTime)
			gs.Output.PrintSuccess("Congratulations! You guessed the correct number in %d attempts and %v.\n",
				i+1, elapsedTime.Round(time.Second))
			return
		}

		if guess > gs.SecretNumber {
			gs.Output.PrintFail("Incorrect! The number is less than %d\n\n", guess)
		} else {
			gs.Output.PrintFail("Incorrect! The number is greater than %d\n\n", guess)
		}

		if i+1 == gs.MaxAttempts {
			break
		}

		wantClue, err := gs.Input.promptYN("Do you want a clue? (Y/N): ")
		if err != nil {
			gs.Output.PrintError("%v", err.Error())
		}

		if wantClue == "Y" {
			minClue, maxClue := calculateClue(i+1, gs.SecretNumber)
			gs.Output.PrintInfo("Clue: The number is between %d and %d\n", minClue, maxClue)
		}
	}

	elapsedTime := time.Since(startTime)
	gs.Output.PrintFail("Oops! after %v you didn't make it, the number was %d!\n", elapsedTime.Round(time.Second), gs.SecretNumber)
}
