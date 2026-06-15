package main

import (
	"fmt"
	"time"
)

type ExpenseServiceInterface interface {
	Add(description string, amount float64, category string) (uint, error)
	Update(id uint, description string, amount float64, category string) error
	Delete(id uint) error
	List(filter bool, category string) ([]string, error)
	Summary(filter bool, month uint) (float64, error)
	Clean() error
	Export() error
}

type ExpenseService struct {
	Data *ExpenseData
}

func NewExpenseService(d *ExpenseData) *ExpenseService {
	return &ExpenseService{Data: d}
}

func (s *ExpenseService) Add(description string, amount float64, category string) (uint, error) {
	// load expense store
	es, loadErr := s.Data.LoadExpenseStore()
	if loadErr != nil {
		return 0, loadErr
	}

	// create expense
	es.LastID++
	e := Expense{
		ID:          es.LastID,
		Description: description,
		Amount:      amount,
		Date:        time.Now().Format(time.DateOnly),
	}
	if category != "" {
		e.Category = category
	}

	// add expense to store
	es.Expenses = append(es.Expenses, e)

	// save expenses
	saveErr := s.Data.SaveExpenseStore(es)
	if saveErr != nil {
		return 0, saveErr
	}

	return e.ID, nil
}

func (s *ExpenseService) Update(id uint, description string, amount float64, category string) error {
	// load expense store
	es, loadErr := s.Data.LoadExpenseStore()
	if loadErr != nil {
		return loadErr
	}

	// find expense & update
	var found bool
	var idx uint
	for i, expense := range es.Expenses {
		if expense.ID == id {
			found = true
			idx = uint(i)
			break
		}
	}

	if !found {
		return fmt.Errorf("Expense with ID %d not found.", id)
	}

	e := &es.Expenses[idx]
	if amount != -1.0 {
		e.Amount = amount
	}
	if description != "" {
		e.Description = description
	}
	if category != "" {
		e.Category = category
	}

	// save expenses
	saveErr := s.Data.SaveExpenseStore(es)
	if saveErr != nil {
		return saveErr
	}

	return nil
}

func (s *ExpenseService) Delete(id uint) error {
	// load expense store
	es, loadErr := s.Data.LoadExpenseStore()
	if loadErr != nil {
		return loadErr
	}

	// find expense
	var found bool
	for _, expense := range es.Expenses {
		if expense.ID == id {
			found = true
			break
		}
	}

	if !found {
		return fmt.Errorf("Expense with ID %d not found.", id)
	}

	// delete from ExpenseStore
	es.DeleteExpense(id)

	// save expenses
	saveErr := s.Data.SaveExpenseStore(es)
	if saveErr != nil {
		return saveErr
	}

	return nil
}

func (s *ExpenseService) List(filter bool, category string) ([]string, error) {
	// load expense store
	es, loadErr := s.Data.LoadExpenseStore()
	if loadErr != nil {
		return nil, loadErr
	}

	if filter {
		return es.ToStringByCategory(category), nil
	}
	return es.ToString(), nil
}

func (s *ExpenseService) Summary(filter bool, month uint) (float64, error) {
	// load expense store
	es, loadErr := s.Data.LoadExpenseStore()
	if loadErr != nil {
		return 0.0, loadErr
	}

	if filter {
		sum, sumErr := es.SummaryByMonth(time.Now().Year(), month)
		if sumErr != nil {
			return 0.0, sumErr
		}
		return sum, nil
	}

	return es.Summary(), nil
}

func (s *ExpenseService) Clean() error {
	return s.Data.CleanFile()
}

func (s *ExpenseService) Export() error {
	es, loadErr := s.Data.LoadExpenseStore()
	if loadErr != nil {
		return loadErr
	}

	return s.Data.ExportCSV(es)
}
