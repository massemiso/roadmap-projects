/*
Copyright © 2026 massemiso massemiso@proton.me
*/
package cmd

import (
	"fmt"
	"log"
	"os"
	"slices"

	"github-trending/internal/github"
	"github-trending/internal/ui"

	"github.com/spf13/cobra"
)

var (
	duration string
	limit    uint
	long     bool
)

var validDurations []string = []string{
	"day",
	"week",
	"month",
	"year",
}

var rootCmd = &cobra.Command{
	Use:   "github-trending",
	Short: "CLI application that talks to GitHub API and shows the trending repositories",
	Long: `Display the trending repositories in a clear and readable format.
The tool allows users to specify a time range (day, week, month, or year) to filter the trending repositories.
It fetches data from the GitHub API and present it in a user-friendly format.`,

	Run: func(cmd *cobra.Command, args []string) {
		if !slices.Contains(validDurations, duration) {
			fmt.Printf("ERROR! duration has to be one of %v\n", validDurations)
			return
		}

		service := github.NewGitHubService()
		trending, err := service.GetTrendingRepos(duration, limit)
		if err != nil {
			log.Fatalln(err.Error())
			return
		}

		ui := ui.NewUI(os.Stdout)
		ui.PrintRepos(trending, long)
	},
}

func Execute() {
	err := rootCmd.Execute()
	if err != nil {
		os.Exit(1)
	}
}

func init() {
	rootCmd.Flags().StringVar(&duration, "duration", "week", "Specify the time: day, week, month, year")
	rootCmd.Flags().UintVar(&limit, "limit", 10, "Specify the number of repositories to show")
	rootCmd.Flags().BoolVar(&long, "long", false, "If you want to display the entire description")
}
