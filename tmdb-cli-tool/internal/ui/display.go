package ui

import (
	"fmt"
	"io"
	"os"
	"strings"

	"tmdb-cli-tool/internal/tmdb"

	"github.com/olekukonko/tablewriter"
)

type UIInterface interface {
	PrintMovies([]tmdb.Movie, bool)
}

type UI struct {
	Output io.Writer
	Colors Colors
}

func NewUI(out io.Writer) *UI {
	_, noColor := os.LookupEnv("NO_COLOR")
	return &UI{out, NewColors(noColor)}
}

func (ui *UI) PrintMovies(movies []tmdb.Movie, text bool) {
	if text {
		ui.printMoviesAsText(movies)
	} else {
		ui.printMoviesAsTable(movies)
	}
}

func (ui *UI) printMoviesAsTable(movies []tmdb.Movie) {
	table := tablewriter.NewWriter(ui.Output)
	table.Header([]string{
		"TITLE",
		"OVERVIEW",
		"POPULARITY",
		"RELEASE_DATE",
		"VOTE_AVG",
	})

	for _, movie := range movies {
		table.Append([]string{
			fmt.Sprintf("%s%s%s", ui.Colors.Green, movie.Title, ui.Colors.Reset),
			truncate(movie.Overview, 70),
			fmt.Sprintf("%.2f", movie.Popularity),
			fmt.Sprintf("%v", movie.ReleaseDate),
			fmt.Sprintf("%.2f", movie.VoteAverage),
		})
	}
	table.Render()
}

func (ui *UI) printMoviesAsText(movies []tmdb.Movie) {
	for _, movie := range movies {
		fmt.Fprintf(ui.Output, "%s%s%s\n", ui.Colors.Green, strings.ToUpper(movie.Title), ui.Colors.Reset)
		fmt.Fprintf(ui.Output, "%s\n", movie.Overview)
		fmt.Fprintf(ui.Output, "%sPOPULARITY:%s %s%.2f%s\n",
			ui.Colors.Underline, ui.Colors.Reset, ui.Colors.Red, movie.Popularity, ui.Colors.Reset)
		fmt.Fprintf(ui.Output, "%sRELEASE DATE:%s %s%s%s\n",
			ui.Colors.Underline, ui.Colors.Reset, ui.Colors.Red, movie.ReleaseDate, ui.Colors.Reset)
		fmt.Fprintf(ui.Output, "%sVOTE AVERAGE:%s %s%.2f%s\n",
			ui.Colors.Underline, ui.Colors.Reset, ui.Colors.Red, movie.VoteAverage, ui.Colors.Reset)
		fmt.Fprintln(ui.Output, "--------------------------------------")
	}
}

func truncate(str string, max int) string {
	if len(str) > max {
		return str[:max-3] + "..."
	}
	return str
}
