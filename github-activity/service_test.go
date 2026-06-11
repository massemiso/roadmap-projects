package main

import (
	"fmt"
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestGetUserActivity(t *testing.T) {
	// 1. Setup a dummy local server
	server := httptest.NewServer(http.HandlerFunc(
		func(w http.ResponseWriter, r *http.Request) {
			// Verify the request is what we expect
			if r.Method != http.MethodGet {
				t.Errorf("Expected GET request, got %s", r.Method)
			}
			// Simulate GitHub response
			w.WriteHeader(http.StatusOK)
			fmt.Fprint(w, `[{"type": "WatchEvent", "repo": {"name": "test/repo"}}]`)
		},
	))
	defer server.Close()

	// 2. Initialize service pointing to our local server instead of github.com
	service := NewGitHubService()
	service.BaseURL = server.URL + "/%s" // Override URL to point to mock server

	// 3. Execute
	activities, err := service.GetUserActivity("testuser", -1)
	// 4. Verify
	if err != nil {
		t.Fatalf("Expected no error, got %v", err)
	}
	if len(activities) != 1 || activities[0].Type != Watch {
		t.Errorf("Unexpected activities: %+v", activities)
	}
}

func TestGetUserActivity_NotFound(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(
		func(w http.ResponseWriter, r *http.Request) {
			if r.Method != http.MethodGet {
				t.Errorf("Expected GET request, got %s", r.Method)
			}
			w.WriteHeader(http.StatusNotFound)
		},
	))
	defer server.Close()

	service := NewGitHubService()
	service.BaseURL = server.URL + "/%s"

	_, err := service.GetUserActivity("testuser", -1)

	if err == nil {
		t.Fatalf("Expected 'Error: User testuser not found', got %v", err)
	}
}

func TestGetUserActivity_WithLimitOf3(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(
		func(w http.ResponseWriter, r *http.Request) {
			if r.Method != http.MethodGet {
				t.Errorf("Expected GET request, got %s", r.Method)
			}
			w.WriteHeader(http.StatusOK)
			fmt.Fprint(w, `[
			{"type": "WatchEvent", "repo": {"name": "test/repo"}},
			{"type": "WatchEvent", "repo": {"name": "test/repo"}},
			{"type": "WatchEvent", "repo": {"name": "test/repo"}},
			{"type": "WatchEvent", "repo": {"name": "test/repo"}},
			{"type": "WatchEvent", "repo": {"name": "test/repo"}}
			]`)
		},
	))
	defer server.Close()

	service := NewGitHubService()
	service.BaseURL = server.URL + "/%s"

	activities, err := service.GetUserActivity("testuser", 3)
	if err != nil {
		t.Fatalf("Expected no error, got %v", err)
	}
	if len(activities) != 3 || activities[0].Type != Watch {
		t.Errorf("Unexpected activities: %+v", activities)
	}
}
