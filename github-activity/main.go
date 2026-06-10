package main

import (
	"fmt"
	"os"
)

func main() {
	args := os.Args
	view := View{}

	if len(args) != 2 {
		fmt.Println(view.FormatUsage(args[0]))
		os.Exit(1)
	}

	username := args[1]
	service := NewGitHubService()

	activity, err := service.GetUserActivity(username)
	if err != nil {
		fmt.Println(view.FormatError(err.Error()))
		os.Exit(1)
	}
	if len(activity) == 0 {
		fmt.Println(view.FormatWarn("No recent activity found"))
		os.Exit(1)
	}

	fmt.Println(view.FormatActivities(username, activity))
}
