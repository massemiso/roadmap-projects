/*
Copyright © 2026 massemiso massemiso@proton.me
*/
package cmd

import (
	"fmt"
	"log"
	"os"
	"slices"

	"tmdb-cli-tool/internal/tmdb"
	"tmdb-cli-tool/internal/ui"

	"github.com/joho/godotenv"

	"github.com/spf13/cobra"
)

var typeVar string

var validTypes []string = []string{
	"playing",
	"popular",
	"top",
	"upcoming",
}

func runE(
	service tmdb.TMDBServiceInterface,
	ui ui.UIInterface,
	typeArg string,
) error {
	if !slices.Contains(validTypes, typeVar) {
		return fmt.Errorf("type has to be one of %s", validTypes)
	}

	movies, serviceErr := service.FetchMovies(typeVar)
	if serviceErr != nil {
		return serviceErr
	}

	ui.PrintMovies(movies)
	return nil
}

var rootCmd = &cobra.Command{
	Use:   "tmdb-cli-tool",
	Short: "Show the popular, top-rated, upcoming and now playing movies from the TMDB API.",
	Run: func(cmd *cobra.Command, args []string) {
		service := tmdb.NewTMDBService()
		ui := ui.NewUI(os.Stdout)
		if err := runE(service, ui, typeVar); err != nil {
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
	if err := godotenv.Load(); err != nil {
		log.Println("No .env file found")
		os.Exit(1)
	}

	rootCmd.Flags().StringVar(&typeVar, "type", "", "Type of movie you want to see")
}
