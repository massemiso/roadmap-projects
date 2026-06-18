package main

import (
	"bufio"
	"fmt"
	"strconv"
	"strings"
)

type GameInput struct {
	Reader *bufio.Reader
}

func NewGameInput(r *bufio.Reader) *GameInput {
	return &GameInput{
		Reader: r,
	}
}

func (i *GameInput) promptNumber(prompt string, minLimit int, maxLimit int) (uint8, error) {
	var number uint8
	for {
		fmt.Printf("%s", prompt)
		input, err := i.Reader.ReadString('\n')
		if err != nil {
			continue
		}
		n, err := i.sanitizeChoiceNumber(input, minLimit, maxLimit)
		if err != nil {
			fmt.Println(err.Error())
			continue
		}
		number = uint8(n)
		break
	}
	return number, nil
}

func (i *GameInput) promptYN(prompt string) (string, error) {
	fmt.Printf("%s", prompt)
	res, err := i.Reader.ReadString('\n')
	if err != nil {
		return "", err
	}
	return i.sanitizeChoice(res), nil
}

func (i *GameInput) sanitizeChoice(choice string) string {
	return strings.TrimSpace(strings.ToUpper(choice))
}

func (i *GameInput) sanitizeChoiceNumber(choiceNumber string, minLimit int, maxLimit int) (int, error) {
	input := strings.TrimSpace(choiceNumber)
	choice, err := strconv.Atoi(input)
	if err != nil || choice < minLimit || choice > maxLimit {
		return 0,
			fmt.Errorf("Please enter a valid integer between %d and %d.", minLimit, maxLimit)
	}
	return choice, nil
}
