package ui

import (
	"bytes"
	"os"
	"strings"
	"testing"

	"github-trending/internal/github"
)

func TestPrintRepos(t *testing.T) {
	os.Setenv("NO_COLOR", "1")
	defer os.Unsetenv("NO_COLOR")

	buf := new(bytes.Buffer)
	ui := NewUI(buf)

	repos := []github.TrendingRepo{
		{
			FullName:    "test/repo",
			Description: "a very long description that should be truncated because it is definitely over seventy characters long to trigger truncation logic",
			Stars:       100,
			Language:    "Go",
		},
	}

	ui.PrintRepos(repos, false)

	output := buf.String()

	// Verify headers
	if !strings.Contains(output, "FULL NAME") || !strings.Contains(output, "STARS") {
		t.Errorf("Output missing headers: %s", output)
	}

	// Verify content
	if !strings.Contains(output, "test/repo") {
		t.Errorf("Output missing repo name: %s", output)
	}

	// Verify truncation (70 chars limit)
	if strings.Contains(output, "a very long description that should be truncated") {
		if !strings.Contains(output, "...") {
			t.Errorf("Expected truncation, but did not find '...': %s", output)
		}
	}
}
