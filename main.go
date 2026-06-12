package main

import (
	"errors"
	"flag"
	"fmt"
	"os"
)

var (
	c       Colors
	binName string
)

func main() {
	c = importColors()
	args := os.Args
	binName = args[0]
	if len(args) < 2 {
		exit("Usage: " + binName + " [add|update|delete|list|summary] ...")
	}

	var err error
	switch args[1] {
	case "add":
		err = Add()
	case "update":
		err = Update()
	case "delete":
		err = Delete()
	case "list":
		err = List()
	case "summary":
		err = Summary()
	default:
		err = errors.New("Expected 'add', 'list', 'update', 'delete', or 'summary' subcommands")
	}

	if err != nil {
		exit(err.Error())
	}
}

func Add() error {
	cmd := flag.NewFlagSet("add", flag.ExitOnError)
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
	if category != "" {
		fmt.Println(category)
	}

	return nil
}

func Update() error {
	cmd := flag.NewFlagSet("update", flag.ExitOnError)
	idPtr := cmd.Uint("id", 0, "id of the expense that you want to update")
	descriptionPtr := cmd.String("description", "", "new description")
	amountPtr := cmd.Float64("amount", -1.0, "new amount")
	categoryPtr := cmd.String("category", "", "new category")

	cmd.Parse(os.Args[2:])
	id := *idPtr
	description := *descriptionPtr
	amount := *amountPtr
	category := *categoryPtr

	// TODO: use old description, amount or category if not provided
	if id == 0 || description == "" || amount <= 0.0 {
		return errors.New("Usage: " + binName +
			` update --description="your description" --amount=100.0 --category="your category" (optional)`)
	}

	// service logic
	if category != "" {
		fmt.Println(category)
	}

	return nil
}

func Delete() error {
	cmd := flag.NewFlagSet("delete", flag.ExitOnError)
	idPtr := cmd.Uint("id", 0, "id of the expense that delete")

	cmd.Parse(os.Args[2:])
	id := *idPtr

	if id == 0 {
		return errors.New("Usage: " + binName + " delete --id=1")
	}

	// service logic

	return nil
}

func List() error {
	cmd := flag.NewFlagSet("list", flag.ExitOnError)
	var filter bool
	categoryPtr := cmd.String("category", "", "filter by category")

	cmd.Parse(os.Args[2:])
	category := *categoryPtr

	if category != "" {
		filter = true
	}

	// service logic
	fmt.Printf("list filtered? %v\n", filter)

	return nil
}

func Summary() error {
	cmd := flag.NewFlagSet("summary", flag.ExitOnError)
	var filter bool
	monthPtr := cmd.Uint("month", 0, "filter by month")

	cmd.Parse(os.Args[2:])
	month := *monthPtr

	if month > 0 && month < 13 {
		filter = true
	}

	// service logic
	fmt.Printf("summary filtered? %v\n", filter)

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
