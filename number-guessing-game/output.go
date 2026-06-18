package main

import (
	"bufio"
	"fmt"
	"os"
	"os/exec"
	"runtime"
)

type GameOutput struct {
	Writer *bufio.Writer
	Color  Color
}

type Color struct {
	reset  string
	red    string
	green  string
	yellow string
	cyan   string
	blue   string
	purple string
	bold   string
}

func NewGameOutput(w *bufio.Writer) *GameOutput {
	_, noColor := os.LookupEnv("NO_COLOR")
	return &GameOutput{
		Writer: w,
		Color:  *NewColor(noColor),
	}
}

func NewColor(noColor bool) *Color {
	if noColor {
		return &Color{}
	}
	return &Color{
		reset:  "\033[0m",
		red:    "\033[31m",
		green:  "\033[32m",
		yellow: "\033[33m",
		blue:   "\033[34m",
		purple: "\033[35m",
		cyan:   "\033[36m",
		bold:   "\033[1m",
	}
}

func (o *GameOutput) ClearScreen() {
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

func (o *GameOutput) Print(msg string) {
	fmt.Fprint(o.Writer, msg)
	o.Writer.Flush() // Explicitly flush so the user sees it immediately
}

func (o *GameOutput) Println(msg string) {
	fmt.Fprintln(o.Writer, msg)
	o.Writer.Flush()
}

func (o *GameOutput) Printf(format string, args ...any) {
	fmt.Fprintf(o.Writer, format, args...)
	o.Writer.Flush()
}

func (o *GameOutput) PrintDifficulty(format string, difficulty string) {
	fmt.Fprintf(o.Writer, format, o.Color.yellow+o.Color.bold+difficulty+o.Color.reset)
	o.Writer.Flush()
}

func (o *GameOutput) PrintSuccess(format string, args ...any) {
	fmt.Fprint(o.Writer, o.Color.green+o.Color.bold)
	fmt.Fprintf(o.Writer, format, args...)
	fmt.Fprint(o.Writer, o.Color.reset)
	o.Writer.Flush()
}

func (o *GameOutput) PrintError(format string, args ...any) {
	fmt.Fprint(o.Writer, o.Color.red+o.Color.bold)
	fmt.Fprintf(o.Writer, format, args...)
	fmt.Fprint(o.Writer, o.Color.reset)
	o.Writer.Flush()
}

func (o *GameOutput) PrintFail(format string, args ...any) {
	fmt.Fprint(o.Writer, o.Color.purple+o.Color.bold)
	fmt.Fprintf(o.Writer, format, args...)
	fmt.Fprint(o.Writer, o.Color.reset)
	o.Writer.Flush()
}

func (o *GameOutput) PrintInfo(format string, args ...any) {
	fmt.Fprint(o.Writer, o.Color.cyan)
	fmt.Fprintf(o.Writer, format, args...)
	fmt.Fprint(o.Writer, o.Color.reset)
	o.Writer.Flush()
}
