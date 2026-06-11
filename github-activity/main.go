package main

import (
	"errors"
	"flag"
	"fmt"
	"os"
)

func main() {
	// 1. Setup -- flags, env variables
	limitPtr := flag.Int("limit", -1, "how many activity do you want")
	flag.Parse()

	_, noColor := os.LookupEnv("NO_COLOR")
	view := NewView(noColor)

	args := flag.Args()

	// 2. Service init
	service := NewGitHubService()

	// 3. Run
	err := run(service, view, args, *limitPtr)
	if err != nil {
		fmt.Println(view.FormatError(err.Error()))
		os.Exit(1)
	}
}

func run(s GitHubServiceInterface, v View, args []string, limit int) error {
	if len(args) != 1 {
		return errors.New("Usage: github-activity <username>")
	}

	username := args[0]
	activity, err := s.GetUserActivity(username, limit)
	if err != nil {
		return err
	}

	if len(activity) == 0 {
		fmt.Println(v.FormatWarn("No recent activity found"))
		return nil
	}

	fmt.Println(v.FormatActivities(username, activity))
	return nil
}
