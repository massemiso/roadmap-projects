package main

import (
	"bufio"
	"fmt"
	"os"
	"strings"
)

func main() {
	reader := bufio.NewReader(os.Stdin)
	gs := NewGameSession(reader)
	for {
		gs.ResetRound()

		gs.SelectDifficulty()
		fmt.Println()

		gs.RunRound()
		fmt.Println()

		fmt.Printf("Do you want to keep playing (Y/N): ")
		keepPlaying, err := reader.ReadString('\n')
		if err != nil {
			return
		}
		keepPlaying = strings.TrimSpace(strings.ToUpper(keepPlaying))

		if keepPlaying != "Y" {
			break
		}
	}
}
