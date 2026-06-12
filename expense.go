package main

type Expense struct {
	ID          uint    `json:"id"`
	Description string  `json:"description"`
	Amount      float64 `json:"amount"`
	Date        string  `json:"date"` // YYYY-MM-DD
	Category    string  `json:"category,omitempty"`
}

type ExpenseStore struct {
	LastID   uint      `json:"id"`
	Expenses []Expense `json:"expenses"`
}
