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
	Attempts uint8         `json:"attempts"`
	Duration time.Duration `json:"duration"`
	Date     time.Time     `json:"date"`
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
		return err
	}

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
		Duration: duration,
		Date:     time.Now(),
	}

	switch difficulty {
	case "Easy":
		if lb.Easy == nil {
			lb.Easy = &newScore
		} else {
			lb.Easy.checkScore(newScore)
		}

	case "Medium":
		if lb.Medium == nil {
			lb.Medium = &newScore
		} else {
			lb.Medium.checkScore(newScore)
		}

	case "Hard":
		if lb.Hard == nil {
			lb.Hard = &newScore
		} else {
			lb.Hard.checkScore(newScore)
		}
	}
}

func (s *Score) checkScore(other Score) {
	if s.Duration > other.Duration {
		*s = other
	}
}
