package cmd

import (
	"errors"
	"testing"
	"tmdb-cli-tool/internal/tmdb"
)

type MockService struct {
	ShouldError bool
}

func (m *MockService) FetchMovies(typeVar string, lang string) ([]tmdb.Movie, error) {
	if typeVar == "invalid" {
		return nil, errors.New("invalid type")
	}
	if m.ShouldError {
		return nil, errors.New("service error")
	}
	return []tmdb.Movie{{Title: "Test Movie", Popularity: 10.0}}, nil
}

type MockUI struct{}

func (m *MockUI) PrintMovies(movies []tmdb.Movie, text bool) {}

func TestRunE(t *testing.T) {
	ui := &MockUI{}

	t.Run("Valid type", func(t *testing.T) {
		service := &MockService{}
		err := runE(service, ui, "popular", false, "en")
		if err != nil {
			t.Errorf("Expected no error, got %v", err)
		}
	})

	t.Run("Invalid type", func(t *testing.T) {
		service := &MockService{}
		err := runE(service, ui, "invalid", false, "en")
		if err == nil {
			t.Error("Expected error for invalid type, got nil")
		}
	})

	t.Run("Service error", func(t *testing.T) {
		service := &MockService{ShouldError: true}
		err := runE(service, ui, "popular", false, "en")
		if err == nil || err.Error() != "service error" {
			t.Errorf("Expected service error, got %v", err)
		}
	})
}
