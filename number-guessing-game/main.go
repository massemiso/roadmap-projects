package main

import (
	"bufio"
	"fmt"
	"math"
	"math/rand/v2"
	"os"
	"strconv"
	"strings"
	"time"
)

func main() {
	reader := bufio.NewReader(os.Stdin)
	for {
		numToGuess := getRandom()
		attempts := start(reader)
		fmt.Println()

		game(reader, attempts, numToGuess)
		fmt.Println()

		fmt.Printf("Do you want to keep playing (Y/N): ")
		keepPlaying, _ := reader.ReadString('\n')
		keepPlaying = strings.TrimSpace(strings.ToUpper(keepPlaying))

		if keepPlaying != "Y" {
			break
		}
	}
}

func getRandom() uint8 {
	return uint8(rand.UintN(100) + 1)
}

func start(reader *bufio.Reader) uint8 {
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
		input, err := reader.ReadString('\n')
		if err != nil {
			continue
		}
		input = strings.TrimSpace(input)
		choice, err := strconv.Atoi(input)
		if err != nil {
			fmt.Println("Invalid input! Please enter a number (1, 2, or 3).")
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
	return attempts
}

func game(reader *bufio.Reader, attempts uint8, numToGuess uint8) {
	startTime := time.Now()

	for i := range attempts {
		var guess uint8
		for {
			fmt.Printf("Enter your guess: ")
			input, err := reader.ReadString('\n')
			if err != nil {
				continue
			}
			input = strings.TrimSpace(input)
			g, err := strconv.Atoi(input)
			if err != nil || g < 1 || g > 100 {
				fmt.Println("Please enter a valid integer between 1 and 100.")
				continue
			}
			guess = uint8(g)
			break
		}
		if guess == numToGuess {
			elapsedTime := time.Since(startTime)
			fmt.Printf("Congratulations! You guessed the correct number in %d attempts and %v.\n",
				i+1, elapsedTime.Round(time.Second))
			return
		}

		if guess > numToGuess {
			fmt.Printf("Incorrect! The number is less than %d\n\n", guess)
		} else {
			fmt.Printf("Incorrect! The number is greater than %d\n\n", guess)
		}

		if i+1 == attempts {
			break
		}

		fmt.Printf("Do you want a clue? (Y/N): ")
		wantClue, _ := reader.ReadString('\n')
		wantClue = strings.TrimSpace(strings.ToUpper(wantClue))

		if wantClue == "Y" {
			minClue, maxClue := clue(i+1, numToGuess)
			fmt.Printf("\nClue: The number is between %d and %d\n", minClue, maxClue)
		}
	}

	elapsedTime := time.Since(startTime)
	fmt.Printf("Oops! after %v you didn't make it, the number was %d!\n", elapsedTime.Round(time.Second), numToGuess)
}

func clue(attemptsTried uint8, numToGuess uint8) (uint8, uint8) {
	offset := uint8(math.Floor(25 / float64(attemptsTried)))
	var minClue uint8 = 1
	var maxClue uint8 = 100

	if numToGuess > offset {
		minClue = numToGuess - offset
		if minClue == 0 {
			minClue = 1
		}
	}

	if numToGuess+offset <= 100 {
		maxClue = numToGuess + offset
	}

	return minClue, maxClue
}
