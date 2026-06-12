package main

import (
	"encoding/json"
	"os"
)

const permissions = 0o644

type ExpenseData struct {
	file string
}

func NewExpenseData(file string) *ExpenseData {
	return &ExpenseData{
		file: file,
	}
}

func (d *ExpenseData) LoadExpenseStore() (ExpenseStore, error) {
	enc, readErr := os.ReadFile(d.file)
	if readErr != nil {
		if os.IsNotExist(readErr) {
			return ExpenseStore{Expenses: []Expense{}}, nil
		}
		return ExpenseStore{}, readErr
	}

	var es ExpenseStore
	decErr := json.Unmarshal(enc, &es)
	if decErr != nil {
		return ExpenseStore{}, decErr
	}
	return es, nil
}

func (d *ExpenseData) SaveExpenseStore(es ExpenseStore) error {
	enc, encErr := json.Marshal(es)
	if encErr != nil {
		return encErr
	}

	writeErr := os.WriteFile(d.file, enc, permissions)
	if writeErr != nil {
		return writeErr
	}

	return nil
}
