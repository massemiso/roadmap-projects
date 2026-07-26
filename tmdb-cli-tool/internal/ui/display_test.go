package ui

import (
	"bytes"
	"os"
	"strings"
	"testing"
	"tmdb-cli-tool/internal/tmdb"
)

func TestPrintMovies(t *testing.T) {
	os.Setenv("NO_COLOR", "1")
	defer os.Unsetenv("NO_COLOR")

	buf := new(bytes.Buffer)
	ui := NewUI(buf)

	movies := []tmdb.Movie{
		{
			Title:       "Test Movie",
			Overview:    "This is a test description that should be long enough to test truncation",
			Popularity:  10.0,
			ReleaseDate: "2026-01-01",
			VoteAverage: 8.0,
		},
	}

	ui.PrintMovies(movies, false)

	output := buf.String()

	// Verify headers
	if !strings.Contains(output, "TITLE") || !strings.Contains(output, "POPULARITY") {
		t.Errorf("Output missing headers: %s", output)
	}

	// Verify content
	if !strings.Contains(output, "Test Movie") {
		t.Errorf("Output missing movie title: %s", output)
	}
}

func TestPrintMoviesAsText(t *testing.T) {
	os.Setenv("NO_COLOR", "1")
	defer os.Unsetenv("NO_COLOR")

	buf := new(bytes.Buffer)
	ui := NewUI(buf)

	movies := []tmdb.Movie{
		{
			Title:       "Test Movie",
			Overview:    "Test Overview",
			Popularity:  10.0,
			ReleaseDate: "2026-01-01",
			VoteAverage: 8.0,
		},
	}

	ui.PrintMovies(movies, true)

	output := buf.String()

	if !strings.Contains(output, "TEST MOVIE") {
		t.Errorf("Output missing movie title: %s", output)
	}
	if !strings.Contains(output, "POPULARITY: 10.00") {
		t.Errorf("Output missing popularity: %s", output)
	}
}
