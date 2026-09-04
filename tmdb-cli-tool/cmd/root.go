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
	typeVar      string
	text         bool
	lang         string
	export       string
	validTypes   []string = []string{"playing", "popular", "top", "upcoming"}
	validLangs   []string = []string{"en", "es", "fr", "de", "it", "pt", "zh", "ja", "ko", "ru"}
	validExports []string = []string{"csv", "json", "none"}
)

func runE(
	service tmdb.TMDBServiceInterface,
	ui ui.UIInterface,
	typeArg string,
	text bool,
	lang string,
	export string,
) error {
	if !slices.Contains(validTypes, typeArg) {
		return fmt.Errorf("type has to be one of %s", validTypes)
	}

	if !slices.Contains(validExports, export) {
		return fmt.Errorf("export has to be one of %s", validExports)
	}

	if !slices.Contains(validLangs, lang) {
		return fmt.Errorf("lang has to be one of %s", validLangs)
	}

	movies, serviceErr := service.FetchMovies(typeArg, lang)
	if serviceErr != nil {
		return serviceErr
	}

	if export != "none" {
		err := service.ExportMovies(movies, export)
		if err != nil {
			return err
		}
	}

	ui.PrintMovies(movies, text)
	return nil
}

var rootCmd = &cobra.Command{
	Use:   "tmdb-cli-tool",
	Short: "Show the popular, top-rated, upcoming and now playing movies from the TMDB API.",
	Run: func(cmd *cobra.Command, args []string) {
		cache := tmdb.NewCacheService(fmt.Sprintf("%s-%s", typeVar, lang))
		service := tmdb.NewTMDBService(cache)
		ui := ui.NewUI(os.Stdout)
		if err := runE(service, ui, typeVar, text, lang, export); err != nil {
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
	checkApiKey()
	rootCmd.Flags().StringVar(&typeVar, "type", "", "Type of movie you want to see")
	rootCmd.Flags().BoolVar(&text, "text", false, "Show movies info as a continued text")
	rootCmd.Flags().StringVar(&lang, "lang", "en", "In what language you want the data")
	rootCmd.Flags().StringVar(&export, "export", "none", "If you want the data to be exported as json/csv")
}

func checkApiKey() {
	godotenv.Load()
	_, apiKeyExists := os.LookupEnv("TMDB_API_KEY")
	if !apiKeyExists {
		log.Fatalln("No api key found! Exiting")
		os.Exit(1)
	}
}
