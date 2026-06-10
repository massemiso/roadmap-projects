package main

import (
	"fmt"
	"os"
)

func main() {
	args := os.Args

	if len(args) != 2 {
		fmt.Printf("%sUsage: %s <username>%s\n", Red, args[0], Reset)
		os.Exit(1)
	}

	username := args[1]
	service := NewGitHubService()

	activity, err := service.GetUserActivity(username)
	if err != nil {
		fmt.Println(Red, err.Error(), Reset)
		os.Exit(1)
	}
	if len(activity) == 0 {
		fmt.Println(Bold, "No recent activity found", Reset)
		os.Exit(1)
	}

	fmt.Printf("%sRecent activity of %s%s\n", Bold, username, Reset)
	for _, info := range activity {
		fmt.Printf("+ %s", info.GetInfo())
	}
}
