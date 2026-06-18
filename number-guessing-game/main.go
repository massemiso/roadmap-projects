package main

import (
	"bufio"
	"os"
)

func main() {
	reader := bufio.NewReader(os.Stdin)
	writer := bufio.NewWriter(os.Stdout)

	out := NewGameOutput(writer)
	in := NewGameInput(reader, out)
	lb := NewLeaderboard()
	gs := NewGameSession(in, out, lb)
	for {
		out.ClearScreen()

		lbErr := lb.LoadLeaderboard()
		if lbErr != nil {
			out.PrintError("ERROR! Can't load leaderboard!")
		}

		gs.ResetRound()
		gs.SelectDifficulty()
		gs.RunRound()

		lbErr = lb.SaveLeaderboard()
		if lbErr != nil {
			out.PrintError("ERROR! Can't save leaderboard!")
		}

		keepPlaying, err := in.promptYN("Do you want to keep playing (Y/N): ")
		if err != nil {
			return
		}

		if keepPlaying != "Y" {
			break
		}
	}
}
