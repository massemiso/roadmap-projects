package main

import (
	"fmt"
	"os"
	"strconv"
	"strings"
)

const (
	RED   = "\033[31m"
	RESET = "\033[0m"
)

func strToUInt(str string) uint {
	i, err := strconv.ParseUint(str, 10, 64)
	if err != nil {
		fmt.Println(string(RED), "Invalid ID", string(RESET))
		os.Exit(1)
	}
	return uint(i)
}

func main() {
	args := os.Args

	if len(args) < 2 {
		fmt.Printf("Usage: %s [add|update|delete|mark-in-progress|mark-done|list]\n", args[0])
		return
	}

	var err error
	repo := NewTaskRepository("data.json")
	service := NewTaskService(repo)

	switch args[1] {
	case "add":
		err = service.Add(strings.Join(args[2:], " "))
	case "update":
		err = service.Update(strToUInt(args[2]), strings.Join(args[3:], " "))
	case "delete":
		err = service.Delete(strToUInt(args[2]))
	case "mark-in-progress":
		err = service.Mark(strToUInt(args[2]), IN_PROGRESS)
	case "mark-done":
		err = service.Mark(strToUInt(args[2]), DONE)
	case "list":
		var tasksStr []string
		if len(args) < 3 {
			tasksStr = service.GetAll()
		} else {
			switch args[2] {
			case "done":
				tasksStr = service.GetAllByMark(DONE)
			case "todo":
				tasksStr = service.GetAllByMark(TODO)
			case "in-progress":
				tasksStr = service.GetAllByMark(IN_PROGRESS)
			default:
				fmt.Println("Invalid mark")
			}
		}
		print(strings.Join(tasksStr, "\n"))
		fmt.Println()
	default:
		fmt.Println("select something..")
	}

	if err != nil {
		fmt.Println(string(RED), err.Error(), string(RESET))
	}
}
