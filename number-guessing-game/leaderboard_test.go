package main

import (
	"testing"
	"time"
)

func TestIsBeatenBy(t *testing.T) {
	tests := []struct {
		name  string
		score Score
		other Score
		want  bool
	}{
		{
			name: "new score has better time",
			score: Score{
				Attempts: 3,
				Duration: 100,
			},
			other: Score{
				Attempts: 3,
				Duration: 99,
			},
			want: true,
		},
		{
			name: "new score has equal time and less attempts",
			score: Score{
				Attempts: 3,
				Duration: 100,
			},
			other: Score{
				Attempts: 2,
				Duration: 100,
			},
			want: true,
		},
		{
			name: "new score has equal time and more attempts",
			score: Score{
				Attempts: 3,
				Duration: 100,
			},
			other: Score{
				Attempts: 4,
				Duration: 100,
			},
			want: false,
		},
		{
			name: "new score has worst time",
			score: Score{
				Attempts: 3,
				Duration: 100,
			},
			other: Score{
				Attempts: 3,
				Duration: 101,
			},
			want: false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			actual := tt.score.IsBeatenBy(tt.other)
			if actual != tt.want {
				t.Errorf("ERROR! (*Score).IsBeatenBy((*Score)) = %v, expected = %v", actual, tt.want)
			}
		})
	}
}

func TestCheck(t *testing.T) {
	tests := []struct {
		name        string
		leaderboard Leaderboard
		difficulty  string
		attempts    uint8
		duration    time.Duration
		wantEasy    *Score
		wantMedium  *Score
		wantHard    *Score
	}{
		{
			name:        "check easy, score nil",
			leaderboard: Leaderboard{},
			difficulty:  "Easy",
			attempts:    3,
			duration:    100 * time.Millisecond,
			wantEasy: &Score{
				Attempts: 3,
				Duration: 100,
			},
		},
		{
			name: "check easy, score not nil & is beaten by new score",
			leaderboard: Leaderboard{
				Easy: &Score{
					Attempts: 3,
					Duration: 100,
				},
			},
			difficulty: "Easy",
			attempts:   3,
			duration:   99 * time.Millisecond,
			wantEasy: &Score{
				Attempts: 3,
				Duration: 99,
			},
		},
		{
			name: "check easy, score not nil & is NOT beaten by new score",
			leaderboard: Leaderboard{
				Easy: &Score{
					Attempts: 3,
					Duration: 100,
				},
			},
			difficulty: "Easy",
			attempts:   3,
			duration:   101 * time.Millisecond,
			wantEasy: &Score{
				Attempts: 3,
				Duration: 100,
			},
		},
		{
			name:        "check medium, score nil",
			leaderboard: Leaderboard{},
			difficulty:  "Medium",
			attempts:    3,
			duration:    100 * time.Millisecond,
			wantMedium: &Score{
				Attempts: 3,
				Duration: 100,
			},
		},
		{
			name: "check medium, score not nil & is beaten by new score",
			leaderboard: Leaderboard{
				Medium: &Score{
					Attempts: 3,
					Duration: 100,
				},
			},
			difficulty: "Medium",
			attempts:   3,
			duration:   99 * time.Millisecond,
			wantMedium: &Score{
				Attempts: 3,
				Duration: 99,
			},
		},
		{
			name: "check medium, score not nil & is NOT beaten by new score",
			leaderboard: Leaderboard{
				Medium: &Score{
					Attempts: 3,
					Duration: 100,
				},
			},
			difficulty: "Medium",
			attempts:   3,
			duration:   101 * time.Millisecond,
			wantMedium: &Score{
				Attempts: 3,
				Duration: 100,
			},
		},
		{
			name:        "check hard, score nil",
			leaderboard: Leaderboard{},
			difficulty:  "Hard",
			attempts:    3,
			duration:    100 * time.Millisecond,
			wantHard: &Score{
				Attempts: 3,
				Duration: 100,
			},
		},
		{
			name: "check hard, score not nil & is beaten by new score",
			leaderboard: Leaderboard{
				Hard: &Score{
					Attempts: 3,
					Duration: 100,
				},
			},
			difficulty: "Hard",
			attempts:   3,
			duration:   99 * time.Millisecond,
			wantHard: &Score{
				Attempts: 3,
				Duration: 99,
			},
		},
		{
			name: "check hard, score not nil & is NOT beaten by new score",
			leaderboard: Leaderboard{
				Hard: &Score{
					Attempts: 3,
					Duration: 100,
				},
			},
			difficulty: "Hard",
			attempts:   3,
			duration:   101 * time.Millisecond,
			wantHard: &Score{
				Attempts: 3,
				Duration: 100,
			},
		},
	}

	// Safe comparison helper function
	assertScore := func(t *testing.T, slotName string, got *Score, want *Score) {
		t.Helper() // Correctly attributes failures to the calling line block
		if want == nil {
			if got != nil {
				t.Errorf("%s score slot: expected nil, got attempts=%d duration=%v", slotName, got.Attempts, got.Duration)
			}
			return
		}
		if got == nil {
			t.Errorf("%s score slot: expected attempts=%d duration=%v, got nil", slotName, want.Attempts, want.Duration)
			return
		}
		if got.Attempts != want.Attempts || got.Duration != want.Duration {
			t.Errorf("%s score slot mismatch:\n got:  attempts=%d, duration=%v\n want: attempts=%d, duration=%v",
				slotName, got.Attempts, got.Duration, want.Attempts, want.Duration)
		}
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			// Work on a copy to ensure scope isolation
			lb := tt.leaderboard

			lb.check(tt.difficulty, tt.attempts, tt.duration)

			// Assert each individual slot cleanly
			assertScore(t, "Easy", lb.Easy, tt.wantEasy)
			assertScore(t, "Medium", lb.Medium, tt.wantMedium)
			assertScore(t, "Hard", lb.Hard, tt.wantHard)
		})
	}
}
