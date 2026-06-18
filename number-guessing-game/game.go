package main

import (
	"bufio"
	"fmt"
	"math/rand/v2"
	"strconv"
	"strings"
	"time"
)

type GameSession struct {
	Reader       *bufio.Reader
	Difficulty   string
	MaxAttempts  uint8
	SecretNumber uint8
	// Leaderboard  *Leaderboard
}

func NewGameSession(r *bufio.Reader) *GameSession {
	return &GameSession{
		Reader: r,
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
		fmt.Printf("\nEnter your choice: ")
		input, err := gs.Reader.ReadString('\n')
		if err != nil {
			continue
		}

		choice, err := sanitizeChoiceNumber(input, 1, 3)
		if err != nil {
			fmt.Println(err.Error())
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

	for i := range gs.MaxAttempts {
		var guess uint8
		for {
			fmt.Printf("Enter your guess: ")
			input, err := gs.Reader.ReadString('\n')
			if err != nil {
				continue
			}
			g, err := sanitizeChoiceNumber(input, 1, 100)
			if err != nil {
				fmt.Println(err.Error())
				continue
			}
			guess = uint8(g)
			break
		}
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

		fmt.Printf("Do you want a clue? (Y/N): ")
		wantClue, err := gs.Reader.ReadString('\n')
		if err != nil {
			return
		}
		wantClue = sanitizeChoice(wantClue)

		if wantClue == "Y" {
			minClue, maxClue := calculateClue(i+1, gs.SecretNumber)
			fmt.Printf("\nClue: The number is between %d and %d\n", minClue, maxClue)
		}
	}

	elapsedTime := time.Since(startTime)
	fmt.Printf("Oops! after %v you didn't make it, the number was %d!\n", elapsedTime.Round(time.Second), gs.SecretNumber)
}

func sanitizeChoice(choice string) string {
	return strings.TrimSpace(strings.ToUpper(choice))
}

func sanitizeChoiceNumber(choiceNumber string, minLimit int, maxLimit int) (int, error) {
	input := strings.TrimSpace(choiceNumber)
	choice, err := strconv.Atoi(input)
	if err != nil || choice < minLimit || choice > maxLimit {
		return 0,
			fmt.Errorf("Please enter a valid integer between %d and %d.", minLimit, maxLimit)
	}
	return choice, nil
}
