package main

import (
	"fmt"
	"testing"
)

func TestDeleteExpense(t *testing.T) {
	testCases := []struct {
		name      string
		es        ExpenseStore
		idDelete  uint
		lengthExp int
	}{
		{
			name: "happy case",
			es: ExpenseStore{
				LastID: 2,
				Expenses: []Expense{
					{ID: 1, Description: "Lunch", Amount: 20.0, Date: "2026-06-15"},
					{ID: 2, Description: "Dinner", Amount: 10.0, Date: "2026-06-15"},
				},
			},
			idDelete:  2,
			lengthExp: 1,
		},
		{
			name: "non-existent expense",
			es: ExpenseStore{
				LastID: 2,
				Expenses: []Expense{
					{ID: 1, Description: "Lunch", Amount: 20.0, Date: "2026-06-15"},
					{ID: 2, Description: "Dinner", Amount: 10.0, Date: "2026-06-15"},
				},
			},
			idDelete:  99,
			lengthExp: 2,
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			tc.es.DeleteExpense(tc.idDelete)
			if len(tc.es.Expenses) != tc.lengthExp {
				t.Fatalf("expected %d expense remaining, got %d", tc.lengthExp, len(tc.es.Expenses))
			}
			if tc.es.Expenses[0].ID != 1 {
				t.Errorf("expected expense ID 1 to remain, got %d", tc.es.Expenses[0].ID)
			}
		})
	}
}

func TestSummary(t *testing.T) {
	testCases := []struct {
		name     string
		store    ExpenseStore
		expected float64
	}{
		{
			name: "happy case",
			store: ExpenseStore{
				Expenses: []Expense{
					{ID: 1, Amount: 20.50},
					{ID: 2, Amount: 10.25},
				},
			},
			expected: 30.75,
		},
		{
			name: "empty store",
			store: ExpenseStore{
				Expenses: []Expense{},
			},
			expected: 0.0,
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			total := tc.store.Summary()
			if total != tc.expected {
				t.Errorf("expected summary to be %.2f, got %.2f", tc.expected, total)
			}
		})
	}
}

func TestSummaryByMonth(t *testing.T) {
	testCases := []struct {
		name     string
		store    ExpenseStore
		month    uint
		expected float64
		wantErr  bool
	}{
		{
			name: "happy case",
			store: ExpenseStore{
				Expenses: []Expense{
					{ID: 1, Amount: 20.0, Date: "2026-06-15"},
					{ID: 2, Amount: 10.0, Date: "2026-06-10"},
					{ID: 3, Amount: 15.0, Date: "2026-05-05"},
					{ID: 4, Amount: 5.0, Date: "2025-06-15"}, // different year
				},
			},
			month:    6,
			expected: 30.00,
		},
		{
			name: "invalid date",
			store: ExpenseStore{
				Expenses: []Expense{
					{ID: 1, Amount: 10.0, Date: "invalid-date"},
				},
			},
			month:    6,
			expected: 0.0,
			wantErr:  true,
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			total, err := tc.store.SummaryByMonth(2026, 6)
			if (err != nil) != tc.wantErr {
				t.Fatalf("SummaryByMonth() error = %v, wantErr %v", err, tc.wantErr)
			}
			if total != tc.expected {
				t.Errorf("expected summary for month %d to be %.2f, got %.2f", tc.month, tc.expected, total)
			}
		})
	}
}

func TestToString(t *testing.T) {
	testCases := []struct {
		name     string
		expense  Expense
		expected string
	}{
		{
			name: "happy path",
			expense: Expense{
				ID:          1,
				Description: "Lunch",
				Amount:      20.0,
				Date:        "2026-06-15",
				Category:    "Food",
			},
			expected: fmt.Sprintf("|%-3d|%-12s|%-12s|$%-8.2f|%-12s|",
				1, "2026-06-15", "Lunch", 20.0, "Food"),
		},
		{
			name: "no category",
			expense: Expense{
				ID:          1,
				Description: "Lunch",
				Amount:      20.0,
				Date:        "2026-06-15",
			},
			expected: fmt.Sprintf("|%-3d|%-12s|%-12s|$%-8.2f|%-12s|",
				1, "2026-06-15", "Lunch", 20.0, ""),
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			str := tc.expense.ToString()
			if str != tc.expected {
				t.Errorf("expected ToString() to be:\n%q\ngot:\n%q", tc.expected, str)
			}
		})
	}
}

func TestToCSV(t *testing.T) {
	testCases := []struct {
		name           string
		store          ExpenseStore
		expectedLen    int
		expectedHeader []string
		expectedRow    []string
	}{
		{
			name: "happy path",
			store: ExpenseStore{
				Expenses: []Expense{
					{ID: 1, Description: "Lunch", Amount: 20.0, Date: "2026-06-15", Category: "Food"},
				},
			},
			expectedLen:    2,
			expectedHeader: []string{"ID", "Description", "Amount", "Date", "Category"},
			expectedRow:    []string{"1", "Lunch", "20.00", "2026-06-15", "Food"},
		},
		{
			name: "no expenses",
			store: ExpenseStore{
				Expenses: []Expense{},
			},
			expectedLen:    1,
			expectedHeader: []string{"ID", "Description", "Amount", "Date", "Category"},
			expectedRow:    []string{},
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			csvData := tc.store.ToCSV()
			if len(csvData) != tc.expectedLen {
				t.Fatalf("expected %d CSV records (header + %d data row), got %d",
					tc.expectedLen, tc.expectedLen-1, len(csvData))
			}

			expectedHeader := tc.expectedHeader
			for i, h := range csvData[0] {
				if h != expectedHeader[i] {
					t.Errorf("expected header column %d to be %q, got %q", i, expectedHeader[i], h)
				}
			}

			expectedRow := tc.expectedRow
			if len(expectedRow) <= 1 {
				return
			}
			for i, col := range csvData[1] {
				if col != expectedRow[i] {
					t.Errorf("expected row column %d to be %q, got %q", i, expectedRow[i], col)
				}
			}
		})
	}
}
