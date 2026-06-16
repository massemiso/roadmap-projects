package main

import (
	"errors"
	"flag"
	"fmt"
	"io"
	"os"
	"path/filepath"
	"strings"
	"time"
)

var (
	c       Colors
	binName string
)

const (
	fAdd     string = "add"
	fUpdate  string = "update"
	fDelete  string = "delete"
	fList    string = "list"
	fSummary string = "summary"
	fClean   string = "clean"
	fExport  string = "export"
	fBudget  string = "budget"
)

var commands = []string{
	fAdd,
	fUpdate,
	fDelete,
	fSummary,
	fClean,
	fExport,
	fBudget,
}

// TODO: change sentinels checking for flag Visit() like in Update
var flags = map[string]func(*AppEnv, ExpenseServiceInterface) error{
	fAdd:     (*AppEnv).Add,
	fUpdate:  (*AppEnv).Update,
	fDelete:  (*AppEnv).Delete,
	fList:    (*AppEnv).List,
	fSummary: (*AppEnv).Summary,
	fClean:   (*AppEnv).Clean,
	fExport:  (*AppEnv).Export,
	fBudget:  (*AppEnv).Budget,
}

type AppEnv struct {
	Service ExpenseServiceInterface
	Stdout  io.Writer
	Stderr  io.Writer
	Args    []string
	Colors  Colors
}

func main() {
	// uses relative path
	baseDir, err := os.UserConfigDir()
	if err != nil {
		baseDir = "."
	}
	appFolder := filepath.Join(baseDir, "expense-tracker")
	_ = os.MkdirAll(appFolder, 0o755)

	jsonPath := filepath.Join(appFolder, "expenses.json")
	csvPath := filepath.Join(appFolder, "expenses.csv")

	data := NewExpenseData(jsonPath, csvPath)
	service := NewExpenseService(data)

	env := &AppEnv{
		Service: service,
		Stdout:  os.Stdout,
		Stderr:  os.Stderr,
		Args:    os.Args,
		Colors:  importColors(),
	}

	if err := env.Run(); err != nil {
		fmt.Fprintf(env.Stderr, "%s%s%s\n", env.Colors.Red, err, env.Colors.Reset)
		os.Exit(1)
	}
}

func (env *AppEnv) Run() error {
	c = env.Colors
	args := env.Args
	binName = args[0]
	service := env.Service

	if len(args) < 2 {
		parts := make([]string, len(commands))
		for i, cmd := range commands {
			parts[i] = string(cmd)
		}
		return fmt.Errorf(
			"Usage: %s [%s] ...",
			binName,
			strings.Join(parts, "|"),
		)
	}

	fn, ok := flags[string(args[1])]
	if !ok {
		parts := make([]string, len(commands))
		for i, cmd := range commands {
			parts[i] = string(cmd)
		}
		return fmt.Errorf(
			"Expected %s subcommands...",
			strings.Join(parts, ", "),
		)
	}

	if err := fn(env, service); err != nil {
		return fmt.Errorf("%v", err.Error())
	}
	return nil
}

func (env *AppEnv) Add(service ExpenseServiceInterface) error {
	cmd := flag.NewFlagSet(fAdd, flag.ExitOnError)
	descriptionPtr := cmd.String("description", "", "description of your new expense")
	amountPtr := cmd.Float64("amount", 0.0, "amount of money of your new expense")
	categoryPtr := cmd.String("category", "", "category of your new expense")

	cmd.Parse(env.Args[2:])
	description := *descriptionPtr
	amount := *amountPtr
	category := *categoryPtr

	if description == "" || amount <= 0.0 {
		return errors.New(`Usage: ` + binName +
			` add --description="your description" --amount=100.0 --category="your category" (optional)`)
	}

	// service logic
	id, err := service.Add(description, amount, category)
	if err != nil && id == 0 {
		return err
	}

	fmt.Fprintf(env.Stdout, "%sExpense added successfully (ID: %d)%s\n", c.Green, id, c.Reset)
	if err != nil { // to catch budget warning
		return err
	}
	return nil
}

func (env *AppEnv) Update(service ExpenseServiceInterface) error {
	const NoChange = "__NO_CHANGE__"

	cmd := flag.NewFlagSet(fUpdate, flag.ExitOnError)
	idPtr := cmd.Uint("id", 0, "id of the expense that you want to update")
	descriptionPtr := cmd.String("description", NoChange, "new description")
	amountPtr := cmd.Float64("amount", 0.0, "new amount")
	categoryPtr := cmd.String("category", NoChange, "new category")

	cmd.Parse(env.Args[2:])
	id := *idPtr
	description := *descriptionPtr
	amount := *amountPtr
	category := *categoryPtr

	// custom bool to check if a flag was set
	passedFlags := make(map[string]bool)
	cmd.Visit(func(f *flag.Flag) {
		passedFlags[f.Name] = true
	})

	// if user don't pass ANY flag
	if !passedFlags["id"] || (!passedFlags["description"] && !passedFlags["amount"] && !passedFlags["category"]) {
		return errors.New("Usage: " + binName +
			` update --id=1 --description="your description" --amount=100.0 --category="your category" (optional)`)
	}
	if passedFlags["amount"] && amount < 0.0 {
		return errors.New("Amount CAN'T be a negative number!")
	}

	// service logic
	modified, err := service.Update(id, description, amount, category)
	if !modified && err != nil {
		return err
	}

	fmt.Fprintf(env.Stdout, "%sExpense updated successfully (ID: %d)%s\n", c.Green, id, c.Reset)
	if err != nil { // to catch budget warning
		return err
	}
	return nil
}

func (env *AppEnv) Delete(service ExpenseServiceInterface) error {
	cmd := flag.NewFlagSet(fDelete, flag.ExitOnError)
	idPtr := cmd.Uint("id", 0, "id of the expense that delete")

	cmd.Parse(env.Args[2:])
	id := *idPtr

	if id == 0 {
		return errors.New("Usage: " + binName + " delete --id=1")
	}

	// service logic
	err := service.Delete(id)
	if err != nil {
		return err
	}

	fmt.Fprintf(env.Stdout, "%sExpense deleted successfully (ID: %d)%s\n", c.Green, id, c.Reset)
	return nil
}

func (env *AppEnv) List(service ExpenseServiceInterface) error {
	cmd := flag.NewFlagSet(fList, flag.ExitOnError)
	var filter bool
	categoryPtr := cmd.String("category", "", "filter by category")

	cmd.Parse(env.Args[2:])
	category := *categoryPtr

	if category != "" {
		filter = true
	}

	// service logic
	exStr, err := service.List(filter, category)
	if err != nil {
		return err
	}

	if len(exStr) == 0 {
		fmt.Fprintf(env.Stdout, "%sThere are no expenses registered!%s\n", c.Magenta, c.Reset)
		return nil
	}

	fmt.Fprintf(env.Stdout, "%s|%-3s|%-12s|%-12s|%-9s|%-12s|%s\n",
		c.Bold, "ID", "Date", "Description", "Amount", "Category", c.Reset)
	fmt.Fprintf(env.Stdout, "%s", c.Blue)
	for _, str := range exStr {
		fmt.Fprintln(env.Stdout, str)
	}
	fmt.Fprintf(env.Stdout, "%s", c.Reset)

	return nil
}

func (env *AppEnv) Summary(service ExpenseServiceInterface) error {
	cmd := flag.NewFlagSet(fSummary, flag.ExitOnError)
	var filter bool
	monthPtr := cmd.Uint("month", 0, "filter by month")

	cmd.Parse(env.Args[2:])
	month := *monthPtr

	cmd.Visit(func(f *flag.Flag) {
		if f.Name == "month" {
			filter = true
		}
	})

	if filter && (month < 1 || month > 12) {
		return errors.New("Month CAN'T be less than 1 or greater than 12!")
	}

	// service logic
	sum, err := service.Summary(filter, month)
	if err != nil {
		return err
	}

	fmt.Fprintf(env.Stdout, "%sTotal expenses", c.Green)
	if filter {
		fmt.Fprintf(env.Stdout, " for %s", time.Month(month).String())
	}
	fmt.Fprintf(env.Stdout, ": $%.2f%s\n", sum, c.Reset)

	return nil
}

func (env *AppEnv) Clean(service ExpenseServiceInterface) error {
	// service logic
	err := service.Clean()
	if err != nil {
		return err
	}

	fmt.Fprintf(env.Stdout, "%sExpenses cleared successfully%s\n", c.Green, c.Reset)
	return nil
}

func (env *AppEnv) Export(service ExpenseServiceInterface) error {
	// service logic
	err := service.Export()
	if err != nil {
		return err
	}

	fmt.Fprintf(env.Stdout, "%sExpenses exported to '%s' successfully%s\n",
		c.Green, service.GetData().GetCSVFile(), c.Reset)
	return nil
}

func (env *AppEnv) Budget(service ExpenseServiceInterface) error {
	cmd := flag.NewFlagSet(fBudget, flag.ExitOnError)
	monthPtr := cmd.Uint("month", 0, "month to set budget")
	amountPtr := cmd.Float64("amount", -1.0, "your budget amount of money")

	cmd.Parse(env.Args[2:])
	month := *monthPtr
	amount := *amountPtr

	if (month <= 0 || month > 12) || amount < 0.0 {
		return errors.New(`Usage: ` + binName +
			` budget --month=1 --amount=100.0`)
	}

	// service logic
	err := service.Budget(month, amount)
	if err != nil {
		return err
	}

	fmt.Fprintf(env.Stdout, "%sSet budget $%.2f for %s successfully!%s\n",
		c.Green, amount, time.Month(month).String(), c.Reset)
	return nil
}

func importColors() Colors {
	_, noColor := os.LookupEnv("NO_COLOR")
	return NewColors(noColor)
}
