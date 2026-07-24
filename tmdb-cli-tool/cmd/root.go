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

var (
	typeVar string
	text    bool
	lang    string
)

var validTypes []string = []string{
	"playing",
	"popular",
	"top",
	"upcoming",
}

var validLangs []string = []string{
	"en",
	"es",
	"fr",
	"de",
	"it",
	"pt",
	"zh",
	"ja",
	"ko",
	"ru",
}

func runE(
	service tmdb.TMDBServiceInterface,
	ui ui.UIInterface,
	typeArg string,
	text bool,
	lang string,
) error {
	if !slices.Contains(validTypes, typeVar) {
		return fmt.Errorf("type has to be one of %s", validTypes)
	}

	if !slices.Contains(validLangs, lang) {
		return fmt.Errorf("lang has to be one of %s", validLangs)
	}

	movies, serviceErr := service.FetchMovies(typeArg, lang)
	if serviceErr != nil {
		return serviceErr
	}

	ui.PrintMovies(movies, text)
	return nil
}

var rootCmd = &cobra.Command{
	Use:   "tmdb-cli-tool",
	Short: "Show the popular, top-rated, upcoming and now playing movies from the TMDB API.",
	Run: func(cmd *cobra.Command, args []string) {
		service := tmdb.NewTMDBService()
		ui := ui.NewUI(os.Stdout)
		if err := runE(service, ui, typeVar, text, lang); err != nil {
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
	rootCmd.Flags().BoolVar(&text, "text", false, "Show movies info as a continued text")
	rootCmd.Flags().StringVar(&lang, "lang", "en", "In what language you want the data")
}
