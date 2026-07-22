/*
Copyright © 2026 massemiso massemiso@proton.me
*/
package cmd

import (
	"fmt"
	"os"

	"github.com/spf13/cobra"
)

var (
	duration string
	limit    int
)

var rootCmd = &cobra.Command{
	Use:   "github-trending",
	Short: "CLI application that talks to GitHub API and shows the trending repositories",
	Long: `Display the trending repositories in a clear and readable format.
The tool allows users to specify a time range (day, week, month, or year) to filter the trending repositories.
It fetches data from the GitHub API and present it in a user-friendly format.`,
	Run: func(cmd *cobra.Command, args []string) {
		fmt.Printf("RUNNING! duration=%s, limit=%d\n", duration, limit)
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
	rootCmd.Flags().IntVar(&limit, "limit", 10, "Specify the number of repositories to show")
}
