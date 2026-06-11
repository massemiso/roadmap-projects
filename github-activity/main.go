package main

import (
	"flag"
	"fmt"
	"os"
)

func main() {
	limitPtr := flag.Int("limit", -1, "how many activity do you want")
	flag.Parse()

	args := flag.Args()
	view := View{}

	if len(args) != 1 {
		fmt.Println(view.FormatUsage("github-activity"))
		os.Exit(1)
	}

	username := args[0]
	service := NewGitHubService()

	activity, err := service.GetUserActivity(username, *limitPtr)
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
