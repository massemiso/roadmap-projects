package main

import (
	"fmt"
	"strings"
	"time"
)

type EventFormatter func(ua *UserActivity) string

var eventFormatters = map[Event]EventFormatter{
	CommitComment: func(ua *UserActivity) string {
		var sb strings.Builder

		var p PayloadCommitComment
		p.GetData(ua.Payload)
		action := capitalize(p.Action)

		sb.WriteString(action)
		sb.WriteString("a commit comment on %s")

		sb.WriteString("\n\t* Body: ")
		if len(p.Comment.Body) >= 20 {
			sb.WriteString(string(p.Comment.Body[0:20]))
		} else {
			sb.WriteString(p.Comment.Body)
		}
		sb.WriteString("\n\t* URL: ")
		sb.WriteString(p.Comment.URL)

		return sb.String()
	},
	Create: func(ua *UserActivity) string {
		var sb strings.Builder
		sb.WriteString("Created a branch/tag on %s")

		var p PayloadCreate
		p.GetData(ua.Payload)

		if p.Description != "" {
			sb.WriteString("\n\t* ")
			sb.WriteString(p.Description)
		}
		return sb.String()
	},
	Delete: func(ua *UserActivity) string {
		return "Deleted a branch/tag on %s"
	},
	Discussion: func(ua *UserActivity) string {
		return "Started a discussion on %s"
	},
	Fork: func(ua *UserActivity) string {
		return "Forked %s"
	},
	Gollum: func(ua *UserActivity) string {
		return "Created/Updated a wiki page on %s"
	},
	IssueComment: func(ua *UserActivity) string {
		var p PayloadIssueComment
		p.GetData(ua.Payload)

		action := capitalize(p.Action)
		out := fmt.Sprintf("%s an issue comment (%d) for issue %d", action, p.Comment.ID, p.Issue.Number)
		out += " on %s"
		out += "\n\t* Body: "
		if len(p.Comment.Body) >= 20 {
			out += string(p.Comment.Body[0:20])
		} else {
			out += p.Comment.Body
		}
		out += fmt.Sprintf("\n\t* URL: %s", p.Comment.HtmlURL)
		return out
	},
	Issues: func(ua *UserActivity) string {
		return "Made an activity in an issue on %s"
	},
	Member: func(ua *UserActivity) string {
		return "Made an activity in a collaboration on %s"
	},
	Public: func(ua *UserActivity) string {
		return "Updated %s to public"
	},
	PullRequest: func(ua *UserActivity) string {
		var p PayloadPullRequest
		p.GetData(ua.Payload)

		action := capitalize(p.Action)
		out := fmt.Sprintf("%s a pull request (%d)", action, p.Number)
		out += " on %s"
		return out
	},
	PullRequestReview: func(ua *UserActivity) string {
		return "Made an activity in a pull request review on %s"
	},
	PullRequestReviewComment: func(ua *UserActivity) string {
		return "Made an activity in a pull request review comment on %s"
	},
	Push: func(ua *UserActivity) string {
		return "Pushed commit to %s"
	},
	Release: func(ua *UserActivity) string {
		return "Made an activity related to a release on %s"
	},
	Watch: func(ua *UserActivity) string {
		return "Starred %s"
	},
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

func capitalize(word string) string {
	return strings.ToUpper(string(word[0])) + string(word[1:])
}
