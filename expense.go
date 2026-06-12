package main

import (
	"fmt"
	"slices"
	"time"
)

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

func (es *ExpenseStore) DeleteExpense(id uint) {
	es.Expenses = slices.DeleteFunc(es.Expenses, func(e Expense) bool {
		return e.ID == id
	})
}

func (e *Expense) ToString() string {
	return fmt.Sprintf("|%-3d|%-12s|%-12s|$%-6.2f|", e.ID, e.Date, e.Description, e.Amount)
}

func (es *ExpenseStore) ToString() []string {
	out := []string{}
	for _, expense := range es.Expenses {
		out = append(out, expense.ToString())
	}
	return out
}

func (es *ExpenseStore) ToStringByCategory(category string) []string {
	out := []string{}
	for _, expense := range es.Expenses {
		if expense.Category != category {
			continue
		}
		out = append(out, expense.ToString())
	}
	return out
}

func (es *ExpenseStore) Summary() float64 {
	var out float64
	for _, expense := range es.Expenses {
		out += expense.Amount
	}
	return out
}

func (es *ExpenseStore) SummaryByMonth(month uint) (float64, error) {
	var out float64
	for _, expense := range es.Expenses {

		exDate, errParse := time.Parse("2006-01-02", expense.Date)
		if errParse != nil {
			return 0.0, errParse
		}

		if uint(exDate.Month()) == month {
			out += expense.Amount
		}
	}
	return out, nil
}
