package main

import "math"

func calculateClue(attemptsTried uint8, numToGuess uint8) (uint8, uint8) {
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
