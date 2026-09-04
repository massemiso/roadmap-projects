package github

import (
	"os"
	"testing"
)

func TestExportJson(t *testing.T) {
	repos := []TrendingRepo{
		{FullName: "a/b", Description: "desc", Stars: 1, Language: "Go"},
	}
	fileName := "test_export"
	tmpDir := t.TempDir()
	originalWd, _ := os.Getwd()
	os.Chdir(tmpDir)
	defer os.Chdir(originalWd)

	err := ExportJson(fileName, repos)
	if err != nil {
		t.Fatalf("Expected no error, got %v", err)
	}

	if _, err := os.Stat(fileName + ".json"); os.IsNotExist(err) {
		t.Errorf("Expected %s.json to exist", fileName)
	}
}

func TestExportCsv(t *testing.T) {
	repos := []TrendingRepo{
		{FullName: "a/b", Description: "desc", Stars: 1, Language: "Go"},
	}
	headers := []string{"FULL_NAME", "DESCRIPTION", "STARS", "LANGUAGE"}
	fileName := "test_export"
	tmpDir := t.TempDir()
	originalWd, _ := os.Getwd()
	os.Chdir(tmpDir)
	defer os.Chdir(originalWd)

	err := ExportCsv(fileName, headers, repos)
	if err != nil {
		t.Fatalf("Expected no error, got %v", err)
	}

	if _, err := os.Stat(fileName + ".csv"); os.IsNotExist(err) {
		t.Errorf("Expected %s.csv to exist", fileName)
	}
}
