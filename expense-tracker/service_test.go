package main

import (
	"errors"
	"os"
	"path/filepath"
	"strings"
	"testing"
	"time"
)

func TestServiceAdd(t *testing.T) {
	tempDir := t.TempDir()
	jsonPath := filepath.Join(tempDir, "expenses_add.json")

	data := NewExpenseData(jsonPath, "")
	service := NewExpenseService(data)

	tests := []struct {
		name        string
		description string
		amount      float64
		category    string
		wantID      uint
		wantErr     bool
	}{
		{
			name:        "add first expense",
			description: "Lunch",
			amount:      15.50,
			category:    "Food",
			wantID:      1,
			wantErr:     false,
		},
		{
			name:        "add second expense",
			description: "Taxi",
			amount:      25.00,
			category:    "Transport",
			wantID:      2,
			wantErr:     false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			id, err := service.Add(tt.description, tt.amount, tt.category)
			if (err != nil) != tt.wantErr {
				t.Fatalf("Add() error = %v, wantErr %v", err, tt.wantErr)
			}
			if id != tt.wantID {
				t.Errorf("Add() got ID = %v, want %v", id, tt.wantID)
			}
		})
	}
}

func TestServiceList(t *testing.T) {
	tempDir := t.TempDir()
	jsonPath := filepath.Join(tempDir, "expenses_list.json")

	data := NewExpenseData(jsonPath, "")
	service := NewExpenseService(data)

	// Pre-populate database
	_, _ = service.Add("Lunch", 15.50, "Food")
	_, _ = service.Add("Taxi", 25.00, "Transport")

	tests := []struct {
		name      string
		filter    bool
		category  string
		wantCount int
		wantErr   bool
	}{
		{
			name:      "list all without filter",
			filter:    false,
			category:  "",
			wantCount: 2,
			wantErr:   false,
		},
		{
			name:      "list filtered by Food",
			filter:    true,
			category:  "Food",
			wantCount: 1,
			wantErr:   false,
		},
		{
			name:      "list filtered by Transport",
			filter:    true,
			category:  "Transport",
			wantCount: 1,
			wantErr:   false,
		},
		{
			name:      "list filtered by non-existent category",
			filter:    true,
			category:  "Utilities",
			wantCount: 0,
			wantErr:   false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			list, err := service.List(tt.filter, tt.category)
			if (err != nil) != tt.wantErr {
				t.Fatalf("List() error = %v, wantErr %v", err, tt.wantErr)
			}
			if len(list) != tt.wantCount {
				t.Errorf("List() got count = %d, want %d", len(list), tt.wantCount)
			}
		})
	}
}

func TestServiceUpdate(t *testing.T) {
	const NoChange = "__NO_CHANGE__"
	tempDir := t.TempDir()
	jsonPath := filepath.Join(tempDir, "expenses_update.json")

	data := NewExpenseData(jsonPath, "")
	service := NewExpenseService(data)

	// Pre-populate with one item (will get ID: 1)
	id, _ := service.Add("Lunch", 15.50, "Food")

	tests := []struct {
		name         string
		id           uint
		description  string
		amount       float64
		category     string
		errContains  string
		checkDesc    string
		checkAmount  float64
		checkCat     string
		wantModified bool
	}{
		{
			name:         "full update",
			id:           id,
			description:  "Business Lunch",
			amount:       18.00,
			category:     "Work",
			errContains:  "",
			checkDesc:    "Business Lunch",
			checkAmount:  18.00,
			checkCat:     "Work",
			wantModified: true,
		},
		{
			name:         "partial update description only",
			id:           id,
			description:  "Only Desc Updated",
			amount:       -1.0,
			category:     NoChange,
			errContains:  "",
			checkDesc:    "Only Desc Updated",
			checkAmount:  18.00,
			checkCat:     "Work",
			wantModified: true,
		},
		{
			name:         "update nonexistent expense",
			id:           999,
			description:  "Ghost Expense",
			amount:       20.0,
			category:     "Ghost",
			errContains:  "not found",
			wantModified: false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			modified, err := service.Update(tt.id, tt.description, tt.amount, tt.category)

			if tt.errContains != "" {
				if err == nil {
					t.Fatalf("expected error containing %q, got nil", tt.errContains)
				}
				if !strings.Contains(err.Error(), tt.errContains) {
					t.Errorf("expected error to contain %q, got: %v", tt.errContains, err)
				}
				if modified != tt.wantModified {
					t.Errorf("expected modified = %v, got %v", tt.wantModified, modified)
				}
				return
			}

			if err != nil {
				t.Fatalf("unexpected update error: %v", err)
			}

			if modified != tt.wantModified {
				t.Errorf("expected modified = %v, got %v", tt.wantModified, modified)
			}

			// Verify fields in storage
			store, err := data.LoadExpenseStore()
			if err != nil {
				t.Fatalf("failed to load store: %v", err)
			}
			var found bool
			for _, e := range store.Expenses {
				if e.ID == tt.id {
					found = true
					if e.Description != tt.checkDesc {
						t.Errorf("expected description %q, got %q", tt.checkDesc, e.Description)
					}
					if e.Amount != tt.checkAmount {
						t.Errorf("expected amount %.2f, got %.2f", tt.checkAmount, e.Amount)
					}
					if e.Category != tt.checkCat {
						t.Errorf("expected category %q, got %q", tt.checkCat, e.Category)
					}
				}
			}
			if !found {
				t.Errorf("expected to find updated expense with ID %d", tt.id)
			}
		})
	}
}

func TestServiceDelete(t *testing.T) {
	tempDir := t.TempDir()
	jsonPath := filepath.Join(tempDir, "expenses_delete.json")

	data := NewExpenseData(jsonPath, "")
	service := NewExpenseService(data)

	// Pre-populate with one item (will get ID: 1)
	id, _ := service.Add("Lunch", 15.50, "Food")

	tests := []struct {
		name        string
		id          uint
		errContains string
		wantCount   int
	}{
		{
			name:        "delete nonexistent item",
			id:          999,
			errContains: "not found",
			wantCount:   1, // original item remains
		},
		{
			name:        "delete valid item",
			id:          id,
			errContains: "",
			wantCount:   0, // empty after delete
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			err := service.Delete(tt.id)

			if tt.errContains != "" {
				if err == nil {
					t.Fatalf("expected error containing %q, got nil", tt.errContains)
				}
				if !strings.Contains(err.Error(), tt.errContains) {
					t.Errorf("expected error to contain %q, got: %v", tt.errContains, err)
				}
			} else if err != nil {
				t.Fatalf("unexpected delete error: %v", err)
			}

			// Verify remaining count
			store, _ := data.LoadExpenseStore()
			if len(store.Expenses) != tt.wantCount {
				t.Errorf("expected %d items remaining, got %d", tt.wantCount, len(store.Expenses))
			}
		})
	}
}

func TestServiceSummary(t *testing.T) {
	tempDir := t.TempDir()
	jsonPath := filepath.Join(tempDir, "expenses_summary.json")

	data := NewExpenseData(jsonPath, "")
	service := NewExpenseService(data)

	// Pre-populate
	_, _ = service.Add("Lunch", 15.50, "Food")
	_, _ = service.Add("Taxi", 25.00, "Transport")

	currentMonth := uint(time.Now().Month())
	otherMonth := currentMonth + 1
	if otherMonth > 12 {
		otherMonth = 1
	}

	tests := []struct {
		name    string
		filter  bool
		month   uint
		want    float64
		wantErr bool
	}{
		{
			name:    "total summary",
			filter:  false,
			month:   0,
			want:    40.50,
			wantErr: false,
		},
		{
			name:    "summary filtered by current month",
			filter:  true,
			month:   currentMonth,
			want:    40.50,
			wantErr: false,
		},
		{
			name:    "summary filtered by other month",
			filter:  true,
			month:   otherMonth,
			want:    0.0,
			wantErr: false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, err := service.Summary(tt.filter, tt.month)
			if (err != nil) != tt.wantErr {
				t.Fatalf("Summary() error = %v, wantErr %v", err, tt.wantErr)
			}
			if got != tt.want {
				t.Errorf("Summary() got = %.2f, want %.2f", got, tt.want)
			}
		})
	}
}

func TestServiceCleanAndExport(t *testing.T) {
	tempDir := t.TempDir()
	jsonPath := filepath.Join(tempDir, "expenses_clean.json")
	csvPath := filepath.Join(tempDir, "expenses_clean.csv")

	data := NewExpenseData(jsonPath, csvPath)
	service := NewExpenseService(data)

	// 1. Initially Clean should error because file doesn't exist
	err := service.Clean()
	if err != nil {
		t.Error("expected nil, got %w", err) // idem op
	}

	// 2. Add item and export
	_, _ = service.Add("Lunch", 15.50, "Food")
	err = service.Export()
	if err != nil {
		t.Fatalf("failed to export: %v", err)
	}

	// Check CSV was created
	if _, err := os.Stat(csvPath); errors.Is(err, os.ErrNotExist) {
		t.Errorf("expected exported CSV file to exist at %s", csvPath)
	}

	// 3. Clean file
	err = service.Clean()
	if err != nil {
		t.Fatalf("failed to clean: %v", err)
	}

	// Loading store should return empty store
	store, err := data.LoadExpenseStore()
	if err != nil {
		t.Fatalf("failed to load store: %v", err)
	}
	if len(store.Expenses) != 0 {
		t.Errorf("expected 0 expenses after clean, got %d", len(store.Expenses))
	}
}

func TestServiceBudget(t *testing.T) {
	tempDir := t.TempDir()
	jsonPath := filepath.Join(tempDir, "expenses_budget.json")

	data := NewExpenseData(jsonPath, "")
	service := NewExpenseService(data)

	// Set budget for the current month
	currentMonth := uint(time.Now().Month())
	err := service.Budget(currentMonth, 50.00)
	if err != nil {
		t.Fatalf("failed to set budget: %v", err)
	}

	// 1. Add expense under budget
	id, err := service.Add("Lunch", 30.00, "Food")
	if err != nil {
		t.Fatalf("unexpected error adding under budget: %v", err)
	}
	if id != 1 {
		t.Errorf("expected ID 1, got %d", id)
	}

	// 2. Add expense that goes over budget
	id2, err := service.Add("Dinner", 25.00, "Food")
	if err == nil {
		t.Error("expected budget warning error, got nil")
	} else if !strings.Contains(err.Error(), "exceed the monthly budget") {
		t.Errorf("expected budget exceeded warning, got: %v", err)
	}
	// The item should still be added despite the warning error
	if id2 != 2 {
		t.Errorf("expected ID 2, got %d", id2)
	}

	// 3. Update expense that triggers budget warning
	// Update dinner to be even higher (still over budget)
	modified, err := service.Update(id2, "", 30.00, "")
	if err == nil {
		t.Error("expected budget warning error on update, got nil")
	} else if !strings.Contains(err.Error(), "exceed the monthly budget") {
		t.Errorf("expected budget warning error on update, got: %v", err)
	}
	if !modified {
		t.Error("expected modified to be true")
	}
}
