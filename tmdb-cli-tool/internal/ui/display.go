package ui

import (
	"fmt"
	"io"
	"os"

	"tmdb-cli-tool/internal/tmdb"

	"github.com/olekukonko/tablewriter"
)

type UIInterface interface {
	PrintMovies([]tmdb.Movie)
}

type UI struct {
	Output io.Writer
	Colors Colors
}

func NewUI(out io.Writer) *UI {
	_, noColor := os.LookupEnv("NO_COLOR")
	return &UI{out, NewColors(noColor)}
}

func (ui *UI) PrintMovies(movies []tmdb.Movie) {
	table := tablewriter.NewWriter(ui.Output)
	table.Header([]string{
		"TITLE",
		"OVERVIEW",
		"POPULARITY",
		"RELEASE_DATE",
		"VOTE AVERAGE",
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

func truncate(str string, max int) string {
	if len(str) > max {
		return str[:max-3] + "..."
	}
	return str
}
