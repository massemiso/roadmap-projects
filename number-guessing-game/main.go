package main

import (
	"bufio"
	"os"
	"os/exec"
	"runtime"
)

func main() {
	reader := bufio.NewReader(os.Stdin)
	gi := NewGameInput(reader)
	gs := NewGameSession(gi)
	for {
		ClearScreen()
		gs.ResetRound()
		gs.SelectDifficulty()
		gs.RunRound()

		keepPlaying, err := gi.promptYN("Do you want to keep playing (Y/N): ")
		if err != nil {
			return
		}

		if keepPlaying != "Y" {
			break
		}
	}
}

func ClearScreen() {
	if runtime.GOOS == "windows" {
		cmd := exec.Command("cmd", "/c", "cls")
		cmd.Stdout = os.Stdout
		cmd.Run()
	} else {
		cmd := exec.Command("clear")
		cmd.Stdout = os.Stdout
		cmd.Run()
	}
}
