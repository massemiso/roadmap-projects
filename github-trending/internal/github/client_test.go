package github

import (
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestGetTrendingRepos(t *testing.T) {
	// Setup mock server
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		w.Write([]byte(`{
			"total_count": 1,
			"incomplete_results": false,
			"items": [
			{
				"full_name": "test/repo",
				"description": "test description",
				"watchers_count": 10,
				"language": "Go"
			}
			]
		}`))
	}))
	defer server.Close()

	// Initialize service and override BaseURL to point to mock server
	service := NewGitHubService()
	service.BaseURL = server.URL + "/search/repositories?q=%s"

	repos, err := service.GetTrendingRepos("week", 10)
	if err != nil {
		t.Fatalf("Expected no error, got %v", err)
	}

	if len(repos) != 1 {
		t.Fatalf("Expected 1 repo, got %d", len(repos))
	}

	repo := repos[0]
	if repo.FullName != "test/repo" {
		t.Errorf("Expected full_name 'test/repo', got %s", repo.FullName)
	}
	if repo.Description != "test description" {
		t.Errorf("Expected description 'test description', got %s", repo.Description)
	}
	if repo.Stars != 10 {
		t.Errorf("Expected 10 stars, got %d", repo.Stars)
	}
}

func TestGetTrendingRepos_GivenNoItems(t *testing.T) {
	// Setup mock server
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		w.Write([]byte(`{
			"total_count": 0,
			"incomplete_results": false,
			"items": [ ]
		}`))
	}))
	defer server.Close()

	// Initialize service and override BaseURL to point to mock server
	service := NewGitHubService()
	service.BaseURL = server.URL + "/search/repositories?q=%s"

	repos, err := service.GetTrendingRepos("week", 10)
	if err != nil {
		t.Fatalf("Expected no error, got %v", err)
	}

	if len(repos) != 0 {
		t.Fatalf("Expected 0 repos, got %d", len(repos))
	}
}

func TestGetTrendingRepos_Forbidden(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusForbidden)
	}))
	defer server.Close()

	service := NewGitHubService()
	service.BaseURL = server.URL + "/search/repositories?q=%s"

	_, err := service.GetTrendingRepos("week", 10)
	if err == nil || err.Error() != "Error: Forbidden" {
		t.Errorf("Expected Forbidden error, got %v", err)
	}
}

func TestGetTrendingRepos_ServiceUnavailable(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusServiceUnavailable)
	}))
	defer server.Close()

	service := NewGitHubService()
	service.BaseURL = server.URL + "/search/repositories?q=%s"

	_, err := service.GetTrendingRepos("week", 10)
	if err == nil || err.Error() != "Error: Service Unavailable" {
		t.Errorf("Expected Service Unavailable error, got %v", err)
	}
}

func TestGetTrendingRepos_InvalidJSON(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		w.Write([]byte(`{ invalid json `))
	}))
	defer server.Close()

	service := NewGitHubService()
	service.BaseURL = server.URL + "/search/repositories?q=%s"

	_, err := service.GetTrendingRepos("week", 10)
	if err == nil {
		t.Errorf("Expected error for invalid JSON, got nil")
	}
}
