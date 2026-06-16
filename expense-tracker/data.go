package main

import (
	"encoding/csv"
	"encoding/json"
	"fmt"
	"os"
)

const permissions = 0o644

type ExpenseDataInterface interface {
	LoadExpenseStore() (ExpenseStore, error)
	SaveExpenseStore(es ExpenseStore) error
	CleanFile() error
	ExportCSV(es ExpenseStore) error
	GetCSVFile() string
}

type ExpenseData struct {
	json string
	csv  string
}

func NewExpenseData(json string, csv string) *ExpenseData {
	return &ExpenseData{
		json: json,
		csv:  csv,
	}
}

func (d *ExpenseData) LoadExpenseStore() (ExpenseStore, error) {
	enc, readErr := os.ReadFile(d.json)
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

	writeErr := os.WriteFile(d.json, enc, permissions)
	if writeErr != nil {
		return writeErr
	}

	return nil
}

func (d *ExpenseData) CleanFile() error {
	err := os.Remove(d.json)
	if err != nil {
		if os.IsNotExist(err) {
			return nil // idempotent op
		}
	}
	return err
}

func (d *ExpenseData) ExportCSV(es ExpenseStore) error {
	file, writeCsvErr := os.Create(d.csv)
	if writeCsvErr != nil {
		return writeCsvErr
	}
	defer file.Close()

	records := es.ToCSV()
	w := csv.NewWriter(file)
	w.WriteAll(records)

	if err := w.Error(); err != nil {
		return fmt.Errorf("Error exporting expenses to csv: %w", err)
	}

	return nil
}

func (d *ExpenseData) GetCSVFile() string {
	return d.csv
}
