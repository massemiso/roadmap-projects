package main

import (
	"encoding/json"
	"fmt"
	"strings"
	"time"
)

type EventFormatter func(ua *UserActivity) string

var eventFormatters = map[Event]EventFormatter{
	CommitComment: func(ua *UserActivity) string {
		return "Made a commit comment on %s"
	},
	Create: func(ua *UserActivity) string {
		var sb strings.Builder
		sb.WriteString("Created a branch/tag on %s")

		var p PayloadCreate
		json.Unmarshal(ua.Payload, &p)

		if p.Description != "" {
			sb.WriteString("\n\t* ")
			sb.WriteString(p.Description)
		}
		return sb.String()
	},
	Delete:       func(ua *UserActivity) string { return "Deleted a branch/tag on %s" },
	Discussion:   func(ua *UserActivity) string { return "Started a discussion on %s" },
	Fork:         func(ua *UserActivity) string { return "Forked %s" },
	Gollum:       func(ua *UserActivity) string { return "Created/Updated a wiki page on %s" },
	IssueComment: func(ua *UserActivity) string { return "Made an activity in an issue/pull request comment on %s" },
	Issues:       func(ua *UserActivity) string { return "Made an activity in an issue on %s" },
	Member:       func(ua *UserActivity) string { return "Made an activity in a collaboration on %s" },
	Public:       func(ua *UserActivity) string { return "Updated %s to public" },
	PullRequest: func(ua *UserActivity) string {
		var p PayloadPullRequest
		json.Unmarshal(ua.Payload, &p)

		action := strings.ToUpper(string(p.Action[0])) + string(p.Action[1:])
		out := fmt.Sprintf("%s a pull request (%d)", action, p.Number)
		out += " on %s"
		return out
	},
	PullRequestReview:        func(ua *UserActivity) string { return "Made an activity in a pull request review on %s" },
	PullRequestReviewComment: func(ua *UserActivity) string { return "Made an activity in a pull request review comment on %s" },
	Push:                     func(ua *UserActivity) string { return "Pushed commit to %s" },
	Release:                  func(ua *UserActivity) string { return "Made an activity related to a release on %s" },
	Watch:                    func(ua *UserActivity) string { return "Starred %s" },
}

func (ua *UserActivity) GetInfo() (string, string, time.Time) {
	var template string
	formatter, ok := eventFormatters[ua.Type]
	if !ok {
		template = "Performed an unknown action (" + string(ua.Type) + ") on %s"
	} else {
		template = formatter(ua)
	}
	return template, ua.Repo.Name, ua.CreatedAt
}
