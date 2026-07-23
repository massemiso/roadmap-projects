package ui

import (
	"fmt"
	"io"
	"os"

	"github-trending/internal/github"

	"github.com/olekukonko/tablewriter"
)

type UIInterface interface {
	PrintRepos([]github.TrendingRepo, bool)
}

type UI struct {
	Output io.Writer
	Colors Colors
}

func NewUI(out io.Writer) *UI {
	_, noColor := os.LookupEnv("NO_COLOR")
	return &UI{out, NewColors(noColor)}
}

func (ui *UI) PrintRepos(repos []github.TrendingRepo, long bool) {
	table := tablewriter.NewWriter(ui.Output)
	table.Header([]string{
		"FULL NAME",
		"DESCRIPTION",
		"STARS",
		"LANGUAGE",
	})

	for _, repo := range repos {
		description := repo.Description
		if !long {
			description = truncate(repo.Description, 70)
		}
		table.Append([]string{
			fmt.Sprintf("%s%s%s", ui.Colors.Green, repo.FullName, ui.Colors.Reset),
			description,
			fmt.Sprint(repo.Stars),
			repo.Language,
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
