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
	export   string
)

var validDurations []string = []string{
	"day",
	"week",
	"month",
	"year",
}

var validExports []string = []string{
	"csv",
	"json",
	"none",
}

func runE(
	service github.GitHubServiceInterface,
	ui ui.UIInterface,
	duration string,
	limit uint,
	long bool,
	export string,
) error {
	if !slices.Contains(validDurations, duration) {
		return fmt.Errorf("duration has to be one of %v", validDurations)
	}

	if !slices.Contains(validExports, export) {
		return fmt.Errorf("export has to be one of %v", validExports)
	}

	trending, err := service.GetTrendingRepos(duration, limit)
	if err != nil {
		return err
	}

	if !(export == "none") {
		err := service.ExportTrendingRepos(trending, export)
		if err != nil {
			return err
		}
	}

	ui.PrintRepos(trending, long)
	return nil
}

var rootCmd = &cobra.Command{
	Use:   "github-trending",
	Short: "CLI application that talks to GitHub API and shows the trending repositories",
	Long: `Display the trending repositories in a clear and readable format.
The tool allows users to specify a time range (day, week, month, or year) to filter the trending repositories.
It fetches data from the GitHub API and present it in a user-friendly format.`,

	Run: func(cmd *cobra.Command, args []string) {
		cache := github.NewCacheService(fmt.Sprintf("github-trending-%s-%d", duration, limit))
		service := github.NewGitHubService(cache)
		ui := ui.NewUI(os.Stdout)
		if err := runE(service, ui, duration, limit, long, export); err != nil {
			log.Fatalln(err.Error())
		}
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
	rootCmd.Flags().StringVar(&export, "export", "none", "If you want to export the data to an external json or csv file")
}
