package main

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

func (s *ExpenseService) Add(description string, amount float64, category string) error {
	return nil
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
