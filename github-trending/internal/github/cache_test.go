package github

import (
	"os"
	"path/filepath"
	"testing"
)

func TestCacheService_CheckCache_Miss(t *testing.T) {
	cacheDir := t.TempDir()
	service := &CacheService{
		Dir:  cacheDir,
		File: filepath.Join(cacheDir, "nonexistent.json"),
	}

	body, err := service.CheckCache()
	if err == nil {
		t.Error("Expected error for non-existent file, got nil")
	}
	if body != nil {
		t.Errorf("Expected nil body, got %v", body)
	}
}

func TestCacheService_CheckCache_Hit(t *testing.T) {
	cacheDir := t.TempDir()
	cacheFile := filepath.Join(cacheDir, "test.json")
	content := []byte(`{"data":"test"}`)
	os.WriteFile(cacheFile, content, 0o644)

	service := &CacheService{
		Dir:  cacheDir,
		File: cacheFile,
	}

	body, err := service.CheckCache()
	if err != nil {
		t.Fatalf("Expected no error, got %v", err)
	}
	if string(body) != string(content) {
		t.Errorf("Expected %s, got %s", content, body)
	}
}

func TestCacheService_SaveCache(t *testing.T) {
	cacheDir := t.TempDir()
	cacheFile := filepath.Join(cacheDir, "save.json")
	content := []byte(`{"data":"save"}`)

	service := &CacheService{
		Dir:  cacheDir,
		File: cacheFile,
	}

	err := service.SaveCache(content)
	if err != nil {
		t.Fatalf("Expected no error, got %v", err)
	}

	readContent, err := os.ReadFile(cacheFile)
	if err != nil {
		t.Fatalf("Failed to read saved cache file: %v", err)
	}
	if string(readContent) != string(content) {
		t.Errorf("Expected %s, got %s", content, readContent)
	}
}
