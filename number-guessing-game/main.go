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
	gs := NewGameSession(in, out)
	for {
		out.ClearScreen()
		gs.ResetRound()
		gs.SelectDifficulty()
		gs.RunRound()

		keepPlaying, err := in.promptYN("Do you want to keep playing (Y/N): ")
		if err != nil {
			return
		}

		if keepPlaying != "Y" {
			break
		}
	}
}
