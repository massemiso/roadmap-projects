package main

import (
	"fmt"
	"math"
	"math/rand/v2"
)

type HintFunc func(secret uint8) string

func GetRandomHint(attempts uint8, secret uint8) string {
	hints := []HintFunc{
		rangeHint(attempts),
		parityHint(),
		digitsHint(),
	}

	randomFunc := hints[rand.IntN(len(hints))]
	return randomFunc(secret)
}

func rangeHint(attempts uint8) HintFunc {
	return func(secret uint8) string {
		offset := uint8(math.Floor(25 / float64(attempts)))
		minClue := max(1, secret-offset)
		maxClue := min(100, secret+offset)
		return fmt.Sprintf("The number is between %d and %d", minClue, maxClue)
	}
}

func parityHint() HintFunc {
	return func(secret uint8) string {
		if secret%2 == 0 {
			return "The number is Even"
		}
		return "The number is Odd"
	}
}

func digitsHint() HintFunc {
	return func(secret uint8) string {
		if secret < 10 {
			return "The number has one digit"
		}
		if secret == 100 {
			return "The number has two equal digits"
		}

		digits := []uint8{
			secret / 10,
			secret % 10,
		}
		if digits[1] == 0 {
			return "The number has a 0 in it"
		}
		if digits[0] == digits[1] {
			return "The number has two equal digits"
		}
		return fmt.Sprintf("The number has a %d in it", digits[rand.IntN(len(digits))])
	}
}
