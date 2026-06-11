package main

import (
	"errors"
	"testing"
)

type MockService struct {
	activities []UserActivity
	err        error
}

func (m *MockService) GetUserActivity(username string, limit int) ([]UserActivity, error) {
	if m.err != nil {
		return nil, m.err
	}
	return m.activities, nil
}

func TestRun(t *testing.T) {
	view := NewView(true) // no color

	t.Run("Success with activity", func(t *testing.T) {
		mock := &MockService{
			activities: []UserActivity{
				{
					Type: Watch,
					Repo: Repo{Name: "user/repo"},
				},
			},
		}

		err := run(mock, view, []string{"testuser"}, -1)
		if err != nil {
			t.Errorf("Expected nil error, got %v", err)
		}
	})

	t.Run("API Error", func(t *testing.T) {
		expectedErr := errors.New("github api is down")
		mock := &MockService{err: expectedErr}

		err := run(mock, view, []string{"testuser"}, -1)
		if err != expectedErr {
			t.Errorf("Expected error %v, got %v", expectedErr, err)
		}
	})

	t.Run("Invalid Arguments", func(t *testing.T) {
		mock := &MockService{}
		// Passing 0 arguments instead of 1
		err := run(mock, view, []string{}, -1)

		if err == nil {
			t.Error("Expected error for missing arguments, got nil")
		}
	})

	t.Run("No Activities", func(t *testing.T) {
		mock := &MockService{
			activities: []UserActivity{},
		}
		err := run(mock, view, []string{"testuser"}, -1)
		if err != nil {
			t.Errorf("Expected nil for no activities, got %v", err)
		}
	})
}
