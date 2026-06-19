package main

import (
	"encoding/json"
	"os"
	"time"
)

const (
	leaderboardFile string = "leaderboard.json"
)

type Score struct {
	Attempts uint8     `json:"attempts"`
	Duration int64     `json:"duration_ms"`
	Date     time.Time `json:"date"`
}

// Tracks the best (fewest) attempts per difficulty level
type Leaderboard struct {
	Easy   *Score `json:"easy"`
	Medium *Score `json:"medium"`
	Hard   *Score `json:"hard"`
}

func NewLeaderboard() *Leaderboard {
	return &Leaderboard{}
}

func (lb *Leaderboard) LoadLeaderboard() error {
	lbFile, err := os.ReadFile(leaderboardFile)
	if err != nil {
		if os.IsNotExist(err) {
			return nil
		}
		return err
	}

	var aux Leaderboard
	decErr := json.Unmarshal(lbFile, &aux)
	if decErr != nil {
		return decErr
	}

	lb.Easy = aux.Easy
	lb.Medium = aux.Medium
	lb.Hard = aux.Hard

	return nil
}

func (lb *Leaderboard) SaveLeaderboard() error {
	enc, encErr := json.Marshal(lb)
	if encErr != nil {
		return encErr
	}

	writeErr := os.WriteFile(leaderboardFile, enc, 0o644)
	if writeErr != nil {
		return writeErr
	}
	return nil
}

func (lb *Leaderboard) check(difficulty string, attempts uint8, duration time.Duration) {
	newScore := Score{
		Attempts: attempts,
		Duration: duration.Milliseconds(),
		Date:     time.Now(),
	}

	switch difficulty {
	case "Easy":
		if lb.Easy == nil || lb.Easy.IsBeatenBy(newScore) {
			lb.Easy = &newScore
		}

	case "Medium":
		if lb.Medium == nil || lb.Medium.IsBeatenBy(newScore) {
			lb.Medium = &newScore
		}

	case "Hard":
		if lb.Hard == nil || lb.Hard.IsBeatenBy(newScore) {
			lb.Hard = &newScore
		}
	}
}

func (s *Score) IsBeatenBy(other Score) bool {
	if s.Duration > other.Duration {
		return true
	} else if s.Duration == other.Duration && s.Attempts > other.Attempts {
		return true
	}
	return false
}
