package main

import (
	"errors"
	"flag"
	"fmt"
	"os"
	"time"
)

var (
	c       Colors
	binName string
)

const (
	fAdd     = "add"
	fUpdate  = "update"
	fDelete  = "delete"
	fList    = "list"
	fSummary = "summary"
	fClean   = "clean"
	fExport  = "export"
)

func main() {
	c = importColors()
	args := os.Args
	binName = args[0]
	if len(args) < 2 {
		exit("Usage: " + binName + " [add|update|delete|list|summary|clean|export] ...")
	}

	var err error
	switch args[1] {
	case fAdd:
		err = Add()
	case fUpdate:
		err = Update()
	case fDelete:
		err = Delete()
	case fList:
		err = List()
	case fSummary:
		err = Summary()
	case fClean:
		err = Clean()
	case fExport:
		err = Export()
	default:
		err = errors.New("Expected 'add', 'list', 'update', 'delete', 'summary', 'clean' or 'export' subcommands")
	}

	if err != nil {
		exit(err.Error())
	}
}

func Add() error {
	cmd := flag.NewFlagSet(fAdd, flag.ExitOnError)
	descriptionPtr := cmd.String("description", "", "description of your new expense")
	amountPtr := cmd.Float64("amount", 0.0, "amount of money of your new expense")
	categoryPtr := cmd.String("category", "", "category of your new expense")

	cmd.Parse(os.Args[2:])
	description := *descriptionPtr
	amount := *amountPtr
	category := *categoryPtr

	if description == "" || amount <= 0.0 {
		return errors.New(`Usage: ` + binName +
			` add --description="your description" --amount=100.0 --category="your category" (optional)`)
	}

	// service logic
	data := NewExpenseData("expenses.json")
	service := NewExpenseService(data)

	id, err := service.Add(description, amount, category)
	if err != nil {
		return err
	}

	fmt.Printf("%sExpense added successfully (ID: %d)%s\n", c.Green, id, c.Reset)
	return nil
}

func Update() error {
	cmd := flag.NewFlagSet(fUpdate, flag.ExitOnError)
	idPtr := cmd.Uint("id", 0, "id of the expense that you want to update")
	descriptionPtr := cmd.String("description", "", "new description")
	amountPtr := cmd.Float64("amount", -1.0, "new amount")
	categoryPtr := cmd.String("category", "", "new category")

	cmd.Parse(os.Args[2:])
	id := *idPtr
	description := *descriptionPtr
	amount := *amountPtr
	category := *categoryPtr

	if id <= 0 || (description == "" && amount < 0.0 && category == "") {
		return errors.New("Usage: " + binName +
			` update --id=1 --description="your description" --amount=100.0 --category="your category" (optional)`)
	}

	// service logic
	data := NewExpenseData("expenses.json")
	service := NewExpenseService(data)

	err := service.Update(id, description, amount, category)
	if err != nil {
		return err
	}

	fmt.Printf("%sExpense updated successfully (ID: %d)%s\n", c.Green, id, c.Reset)
	return nil
}

func Delete() error {
	cmd := flag.NewFlagSet(fDelete, flag.ExitOnError)
	idPtr := cmd.Uint("id", 0, "id of the expense that delete")

	cmd.Parse(os.Args[2:])
	id := *idPtr

	if id == 0 {
		return errors.New("Usage: " + binName + " delete --id=1")
	}

	// service logic
	data := NewExpenseData("expenses.json")
	service := NewExpenseService(data)

	err := service.Delete(id)
	if err != nil {
		return err
	}

	fmt.Printf("%sExpense deleted successfully (ID: %d)%s\n", c.Green, id, c.Reset)
	return nil
}

func List() error {
	cmd := flag.NewFlagSet(fList, flag.ExitOnError)
	var filter bool
	categoryPtr := cmd.String("category", "", "filter by category")

	cmd.Parse(os.Args[2:])
	category := *categoryPtr

	if category != "" {
		filter = true
	}

	// service logic
	data := NewExpenseData("expenses.json")
	service := NewExpenseService(data)

	exStr, err := service.List(filter, category)
	if err != nil {
		return err
	}

	if len(exStr) == 0 {
		fmt.Printf("%sThere are no expenses registered!%s\n", c.Magenta, c.Reset)
		return nil
	}

	fmt.Printf("%s|%-3s|%-12s|%-12s|%-7s|%s\n", c.Bold, "ID", "Date", "Description", "Amount", c.Reset)
	fmt.Printf("%s", c.Blue)
	for _, str := range exStr {
		fmt.Println(str)
	}
	fmt.Printf("%s", c.Reset)

	return nil
}

func Summary() error {
	cmd := flag.NewFlagSet(fSummary, flag.ExitOnError)
	var filter bool
	monthPtr := cmd.Uint("month", 0, "filter by month")

	cmd.Parse(os.Args[2:])
	month := *monthPtr

	if month > 0 && month < 13 {
		filter = true
	}

	// service logic
	data := NewExpenseData("expenses.json")
	service := NewExpenseService(data)

	sum, err := service.Summary(filter, month)
	if err != nil {
		return err
	}

	fmt.Printf("%sTotal expenses", c.Green)
	if filter {
		fmt.Printf(" for %s", time.Month(month).String())
	}
	fmt.Printf(": $%.2f%s\n", sum, c.Reset)

	return nil
}

func Clean() error {
	// service logic
	data := NewExpenseData("expenses.json")
	service := NewExpenseService(data)

	err := service.Clean()
	if err != nil {
		return err
	}

	fmt.Printf("%sExpenses cleared successfully%s\n", c.Green, c.Reset)
	return nil
}

func Export() error {
	// service logic
	data := NewExpenseDataCSV("expenses.json", "expenses.csv")
	service := NewExpenseService(data)

	err := service.Export()
	if err != nil {
		return err
	}

	fmt.Printf("%sExpenses exported to '%s' successfully%s\n", c.Green, data.csv, c.Reset)
	return nil
}

func importColors() Colors {
	_, noColor := os.LookupEnv("NO_COLOR")
	return NewColors(noColor)
}

func exit(error string) {
	fmt.Printf("%s%s%s\n", c.Red, error, c.Reset)
	os.Exit(1)
}
