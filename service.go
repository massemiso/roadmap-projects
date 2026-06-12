package main

import "time"

type ExpenseServiceInterface interface {
	Add(description string, amount float64, category string) error
	Update(id uint, description string, amount float64, category string) error
	Delete(id uint) error
	List(category string) error
	Summary(month uint) error
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
	return nil
}

func (s *ExpenseService) Delete(id uint) error {
	return nil
}

func (s *ExpenseService) List(category string) error {
	return nil
}

func (s *ExpenseService) Summary(month uint) error {
	return nil
}
