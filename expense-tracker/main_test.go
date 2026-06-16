package main

import (
	"bytes"
	"errors"
	"fmt"
	"strings"
	"testing"
)

type mockExpenseService struct {
	// Fields to verify what main passed to our service
	lastAddedDesc string
	lastAddedAmt  float64
	lastAddedCat  string

	// Stub values to simulate happy paths or failures
	stubID       uint
	stubModified bool
	stubError    error
}

func (m *mockExpenseService) Add(description string, amount float64, category string) (uint, error) {
	m.lastAddedDesc = description
	m.lastAddedAmt = amount
	m.lastAddedCat = category
	return m.stubID, m.stubError
}

func (m *mockExpenseService) Update(id uint, description string, amount float64, category string) (bool, error) {
	if m.stubID != id {
		return false, m.stubError
	}
	if description != "" {
		m.lastAddedDesc = description
	}
	if amount > 0.0 {
		m.lastAddedAmt = amount
	}
	if category != "" {
		m.lastAddedCat = category
	}
	return m.stubModified, m.stubError
}

func (m *mockExpenseService) Delete(id uint) error {
	return m.stubError
}

func (m *mockExpenseService) List(filter bool, category string) ([]string, error) {
	if m.stubError != nil {
		return nil, m.stubError
	}
	return []string{fmt.Sprintf("|%-3d|%-12s|%-12s|$%-8.2f|%-12s|",
		m.stubID, "2026-06-16", m.lastAddedDesc, m.lastAddedAmt, m.lastAddedCat)}, m.stubError
}

func (m *mockExpenseService) Summary(filter bool, month uint) (float64, error) {
	return 100.0, m.stubError
}

func (m *mockExpenseService) Clean() error {
	return m.stubError
}

func (m *mockExpenseService) Export() error {
	return m.stubError
}

func (m *mockExpenseService) Budget(month uint, amount float64) error {
	return m.stubError
}

type mockExpenseData struct {
	ExpenseDataInterface
	stubCSVFile string
}

func (m *mockExpenseData) GetCSVFile() string {
	return m.stubCSVFile
}

// Update your existing mockExpenseService.GetData() method to look like this:
func (m *mockExpenseService) GetData() ExpenseDataInterface {
	return &mockExpenseData{stubCSVFile: "mock_expenses.csv"}
}

func TestRunSubcommands_ValidationAndRouting(t *testing.T) {
	tests := []struct {
		name           string
		args           []string
		stubErr        error
		expectedErrSub string
	}{
		// ============================================================================
		// GLOBAL ROUTING BOUNDARY TESTS
		// ============================================================================
		{
			name:           "Empty args triggers global usage matrix error",
			args:           []string{"expense-tracker"},
			expectedErrSub: "Usage: expense-tracker [add|update|delete|summary|clean|export|budget] ...",
		},
		{
			name:           "Invalid subcommand produces expected error token text",
			args:           []string{"expense-tracker", "unknowncmd"},
			expectedErrSub: "Expected add, update, delete, summary, clean, export, budget subcommands...",
		},

		// ============================================================================
		// SUBCOMMAND: ADD
		// ============================================================================
		{
			name:           "Missing structural arguments on add subcommand triggers format error",
			args:           []string{"expense-tracker", "add"},
			expectedErrSub: "Usage: expense-tracker add",
		},
		{
			name:           "Zero or negative amount triggers clear runtime validation constraints",
			args:           []string{"expense-tracker", "add", "--description=Lunch", "--amount=0.0"},
			expectedErrSub: "Usage: expense-tracker add",
		},

		// ============================================================================
		// SUBCOMMAND: UPDATE
		// ============================================================================
		{
			name:           "Update with completely empty flag variants throws usage matrix",
			args:           []string{"expense-tracker", "update"},
			expectedErrSub: "Usage: expense-tracker update",
		},
		{
			name:           "Update passing fields but missing mandatory ID flag throws usage matrix",
			args:           []string{"expense-tracker", "update", "--description=NewDescription"},
			expectedErrSub: "Usage: expense-tracker update",
		},
		{
			name:           "Update passing ID but missing target alteration values throws usage matrix",
			args:           []string{"expense-tracker", "update", "--id=1"},
			expectedErrSub: "Usage: expense-tracker update",
		},
		{
			name:           "Update specifying a negative financial value returns specific error text",
			args:           []string{"expense-tracker", "update", "--id=1", "--amount=-45.50"},
			expectedErrSub: "Amount CAN'T be a negative number!",
		},

		// ============================================================================
		// SUBCOMMAND: DELETE
		// ============================================================================
		{
			name:           "Delete subcommand missing completely or passing 0 value triggers usage matrix",
			args:           []string{"expense-tracker", "delete", "--id=0"},
			expectedErrSub: "Usage: expense-tracker delete --id=1",
		},

		// ============================================================================
		// SUBCOMMAND: SUMMARY
		// ============================================================================
		{
			name:           "Summary flag evaluating month lower than calendar bounds limits triggers text matrix",
			args:           []string{"expense-tracker", "summary", "--month=0"},
			expectedErrSub: "Month CAN'T be less than 1 or greater than 12!",
		},
		{
			name:           "Summary flag evaluating month higher than calendar bounds limits triggers text matrix",
			args:           []string{"expense-tracker", "summary", "--month=13"},
			expectedErrSub: "Month CAN'T be less than 1 or greater than 12!",
		},

		// ============================================================================
		// SUBCOMMAND: BUDGET
		// ============================================================================
		{
			name:           "Budget command missing options falls back into structural error text",
			args:           []string{"expense-tracker", "budget"},
			expectedErrSub: "Usage: expense-tracker budget --month=1 --amount=100.0",
		},
		{
			name:           "Budget command specifying invalid calendar index evaluates usage layout",
			args:           []string{"expense-tracker", "budget", "--month=15", "--amount=200.0"},
			expectedErrSub: "Usage: expense-tracker budget --month=1 --amount=100.0",
		},
		{
			name:           "Budget command specifying negative constraints throws usage matrix error",
			args:           []string{"expense-tracker", "budget", "--month=5", "--amount=-20.00"},
			expectedErrSub: "Usage: expense-tracker budget --month=1 --amount=100.0",
		},

		// ============================================================================
		// BUBBLING CORE DOMAIN SERVICE FAILURE RUNWAYS
		// ============================================================================
		{
			name:           "Core service engine layer bubble native structural errors safely up the call stack",
			args:           []string{"expense-tracker", "delete", "--id=99"},
			stubErr:        errors.New("sql: database row locked by concurrent thread process context"),
			expectedErrSub: "sql: database row locked by concurrent thread process context",
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			var stdout bytes.Buffer
			var stderr bytes.Buffer

			// Inject our simulated domain stub error variable directly into our service simulator
			mockSvc := &mockExpenseService{
				stubError: tt.stubErr,
				stubID:    42, // Ensure structural valid return data handles internally
			}

			env := &AppEnv{
				Service: mockSvc,
				Stdout:  &stdout,
				Stderr:  &stderr,
				Args:    tt.args,
				Colors:  NewColors(true), // Explicitly omit escape sequence parameters for simple evaluation checks
			}

			err := env.Run()
			if err == nil {
				t.Fatalf("Expected runtime verification error context to throw, but pipeline evaluated cleanly instead")
			}

			if !strings.Contains(err.Error(), tt.expectedErrSub) {
				t.Errorf("Assertion mismatch on error text capture!\nExpected token string element: %q\nReceived full text runtime traceback: %q",
					tt.expectedErrSub, err.Error())
			}
		})
	}
}

func TestAddCommand_SuccessFlow(t *testing.T) {
	var stdout bytes.Buffer
	var stderr bytes.Buffer
	mockSvc := &mockExpenseService{stubID: 42}

	env := &AppEnv{
		Service: mockSvc,
		Stdout:  &stdout,
		Stderr:  &stderr,
		Args:    []string{"expense-tracker", "add", "--description=Burgers", "--amount=15.50", "--category=Food"},
		Colors:  NewColors(true),
	}

	err := env.Run()
	if err != nil {
		t.Fatalf("Expected addition happy-path execution sequence, got: %v", err)
	}

	// Verify that arguments were parsed correctly and sent to our decoupled core domain library
	if mockSvc.lastAddedDesc != "Burgers" || mockSvc.lastAddedAmt != 15.50 || mockSvc.lastAddedCat != "Food" {
		t.Errorf("Service layer variables corrupted! Got description: %q, amount: %f, category: %q",
			mockSvc.lastAddedDesc, mockSvc.lastAddedAmt, mockSvc.lastAddedCat)
	}

	// Verify that success statements were flushed cleanly to our captured Stdout buffer frame
	output := stdout.String()
	if !strings.Contains(output, "Expense added successfully (ID: 42)") {
		t.Errorf("Expected success string to be captured in memory, got instead:\n%s", output)
	}
}

func TestUpdateCommand_SuccessFlow(t *testing.T) {
	var stdout bytes.Buffer
	var stderr bytes.Buffer
	mockSvc := &mockExpenseService{stubID: 42}

	env := &AppEnv{
		Service: mockSvc,
		Stdout:  &stdout,
		Stderr:  &stderr,
		Args:    []string{"expense-tracker", "update", "--id=42", "--description=Burgers", "--amount=15.50", "--category=Food"},
		Colors:  NewColors(true),
	}

	err := env.Run()
	if err != nil {
		t.Fatalf("Expected addition happy-path execution sequence, got: %v", err)
	}

	// Verify that arguments were parsed correctly and sent to our decoupled core domain library
	if mockSvc.lastAddedDesc != "Burgers" || mockSvc.lastAddedAmt != 15.50 || mockSvc.lastAddedCat != "Food" {
		t.Errorf("Service layer variables corrupted! Got description: %q, amount: %f, category: %q",
			mockSvc.lastAddedDesc, mockSvc.lastAddedAmt, mockSvc.lastAddedCat)
	}

	// Verify that success statements were flushed cleanly to our captured Stdout buffer frame
	output := stdout.String()
	if !strings.Contains(output, "Expense updated successfully (ID: 42)") {
		t.Errorf("Expected success string to be captured in memory, got instead:\n%s", output)
	}
}

func TestDeleteCommand_SuccessFlow(t *testing.T) {
	var stdout bytes.Buffer
	var stderr bytes.Buffer
	mockSvc := &mockExpenseService{}

	env := &AppEnv{
		Service: mockSvc,
		Stdout:  &stdout,
		Stderr:  &stderr,
		Args:    []string{"expense-tracker", "delete", "--id=15"},
		Colors:  NewColors(true),
	}

	err := env.Run()
	if err != nil {
		t.Fatalf("Expected successful deletion flow, got: %v", err)
	}

	output := stdout.String()
	if !strings.Contains(output, "Expense deleted successfully (ID: 15)") {
		t.Errorf("Expected delete success message, got instead:\n%s", output)
	}
}

func TestDeleteCommand_ValidationError(t *testing.T) {
	var stdout bytes.Buffer
	var stderr bytes.Buffer
	mockSvc := &mockExpenseService{}

	env := &AppEnv{
		Service: mockSvc,
		Stdout:  &stdout,
		Stderr:  &stderr,
		Args:    []string{"expense-tracker", "delete", "--id=0"}, // 0 is invalid
		Colors:  NewColors(true),
	}

	err := env.Run()
	if err == nil {
		t.Fatal("Expected validation error due to ID=0, but execution succeeded")
	}

	if !strings.Contains(err.Error(), "Usage:") {
		t.Errorf("Expected usage instruction error, got: %v", err)
	}
}

func TestListCommand_SuccessFlow(t *testing.T) {
	var stdout bytes.Buffer
	var stderr bytes.Buffer
	// Pre-populate stub identifiers to verify output matches formatting matrix rules
	mockSvc := &mockExpenseService{
		stubID:        1,
		lastAddedDesc: "Coffee",
		lastAddedAmt:  4.50,
		lastAddedCat:  "Utilities",
	}

	env := &AppEnv{
		Service: mockSvc,
		Stdout:  &stdout,
		Stderr:  &stderr,
		Args:    []string{"expense-tracker", "list", "--category=Utilities"},
		Colors:  NewColors(true),
	}

	err := env.Run()
	if err != nil {
		t.Fatalf("Expected successful listing sequence, got: %v", err)
	}

	output := stdout.String()
	// Verify header layout is printed safely
	if !strings.Contains(output, "ID") || !strings.Contains(output, "Description") {
		t.Errorf("Output table headers missing formatting tokens. Got:\n%s", output)
	}
	// Verify data row matches expected parameters passed down
	if !strings.Contains(output, "Coffee") || !strings.Contains(output, "4.50") {
		t.Errorf("Output table data corrupted. Got:\n%s", output)
	}
}

func TestSummaryCommand_SuccessFlow(t *testing.T) {
	var stdout bytes.Buffer
	var stderr bytes.Buffer
	mockSvc := &mockExpenseService{} // mockSummary returns hardcoded 100.0

	env := &AppEnv{
		Service: mockSvc,
		Stdout:  &stdout,
		Stderr:  &stderr,
		Args:    []string{"expense-tracker", "summary", "--month=8"},
		Colors:  NewColors(true),
	}

	err := env.Run()
	if err != nil {
		t.Fatalf("Expected summary execution context to pass, got: %v", err)
	}

	output := stdout.String()
	if !strings.Contains(output, "Total expenses for August: $100.00") {
		t.Errorf("Expected parsed month label string matching August sum total, got:\n%s", output)
	}
}

func TestSummaryCommand_InvalidMonthError(t *testing.T) {
	var stdout bytes.Buffer
	var stderr bytes.Buffer
	mockSvc := &mockExpenseService{}

	env := &AppEnv{
		Service: mockSvc,
		Stdout:  &stdout,
		Stderr:  &stderr,
		Args:    []string{"expense-tracker", "summary", "--month=13"}, // Invalid month index
		Colors:  NewColors(true),
	}

	err := env.Run()
	if err == nil {
		t.Fatal("Expected error validation for month index out of bounds, but execution succeeded")
	}

	if !strings.Contains(err.Error(), "Month CAN'T be less than 1 or greater than 12") {
		t.Errorf("Unexpected error message variant received: %v", err)
	}
}

func TestCleanCommand_SuccessFlow(t *testing.T) {
	var stdout bytes.Buffer
	var stderr bytes.Buffer
	mockSvc := &mockExpenseService{}

	env := &AppEnv{
		Service: mockSvc,
		Stdout:  &stdout,
		Stderr:  &stderr,
		Args:    []string{"expense-tracker", "clean"},
		Colors:  NewColors(true),
	}

	err := env.Run()
	if err != nil {
		t.Fatalf("Expected clear operation path to succeed, got: %v", err)
	}

	output := stdout.String()
	if !strings.Contains(output, "Expenses cleared successfully") {
		t.Errorf("Expected verification output string message, got:\n%s", output)
	}
}

func TestExportCommand_SuccessFlow(t *testing.T) {
	var stdout bytes.Buffer
	var stderr bytes.Buffer
	mockSvc := &mockExpenseService{}

	env := &AppEnv{
		Service: mockSvc,
		Stdout:  &stdout,
		Stderr:  &stderr,
		Args:    []string{"expense-tracker", "export"},
		Colors:  NewColors(true),
	}

	err := env.Run()
	if err != nil {
		t.Fatalf("Expected export handler sequence to pass cleanly, got: %v", err)
	}

	output := stdout.String()
	if !strings.Contains(output, "Expenses exported to 'mock_expenses.csv' successfully") {
		t.Errorf("CSV placeholder path template string evaluation failed. Got:\n%s", output)
	}
}

func TestBudgetCommand_SuccessFlow(t *testing.T) {
	var stdout bytes.Buffer
	var stderr bytes.Buffer
	mockSvc := &mockExpenseService{}

	env := &AppEnv{
		Service: mockSvc,
		Stdout:  &stdout,
		Stderr:  &stderr,
		Args:    []string{"expense-tracker", "budget", "--month=12", "--amount=500.0"},
		Colors:  NewColors(true),
	}

	err := env.Run()
	if err != nil {
		t.Fatalf("Expected successful budget allocation path, got: %v", err)
	}

	output := stdout.String()
	if !strings.Contains(output, "Set budget $500.00 for December successfully!") {
		t.Errorf("Budget message output label string generation mismatch. Got:\n%s", output)
	}
}

func TestBudgetCommand_ValidationError(t *testing.T) {
	var stdout bytes.Buffer
	var stderr bytes.Buffer
	mockSvc := &mockExpenseService{}

	env := &AppEnv{
		Service: mockSvc,
		Stdout:  &stdout,
		Stderr:  &stderr,
		Args:    []string{"expense-tracker", "budget", "--month=12", "--amount=-10.0"}, // Negative budget
		Colors:  NewColors(true),
	}

	err := env.Run()
	if err == nil {
		t.Fatal("Expected validation rule error processing negative currency numbers, but operation passed")
	}

	if !strings.Contains(err.Error(), "Usage:") {
		t.Errorf("Expected standard usage feedback matrix error, got: %v", err)
	}
}
