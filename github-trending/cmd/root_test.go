package cmd

import (
	"errors"
	"testing"

	"github-trending/internal/github"
)

type MockService struct {
	ShouldError bool
}

func (m *MockService) GetTrendingRepos(duration string, limit uint) ([]github.TrendingRepo, error) {
	if duration == "invalid" {
		return nil, errors.New("invalid duration")
	}
	if m.ShouldError {
		return nil, errors.New("service error")
	}
	return []github.TrendingRepo{{FullName: "test/repo", Stars: 10}}, nil
}

func (m *MockService) ExportTrendingRepos(repos []github.TrendingRepo, format string) error {
	return nil
}

type MockUI struct{}

func (m *MockUI) PrintRepos(repos []github.TrendingRepo, long bool) {}

func TestRunE(t *testing.T) {
	ui := &MockUI{}

	t.Run("Valid duration", func(t *testing.T) {
		service := &MockService{}
		err := runE(service, ui, "week", 10, false, "none")
		if err != nil {
			t.Errorf("Expected no error, got %v", err)
		}
	})

	t.Run("Invalid duration", func(t *testing.T) {
		service := &MockService{}
		err := runE(service, ui, "invalid", 10, false, "none")
		if err == nil {
			t.Error("Expected error for invalid duration, got nil")
		}
	})

	t.Run("Service error", func(t *testing.T) {
		service := &MockService{ShouldError: true}
		err := runE(service, ui, "week", 10, false, "none")
		if err == nil || err.Error() != "service error" {
			t.Errorf("Expected service error, got %v", err)
		}
	})
}
