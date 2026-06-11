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
		var p PayloadDelete
		p.GetData(ua.Payload)
		return fmt.Sprintf("Deleted %s '%s' on %%s", p.RefType, p.Ref)
	},
	Discussion: func(ua *UserActivity) string {
		var p PayloadDiscussion
		p.GetData(ua.Payload)
		return fmt.Sprintf("Started a discussion: '%s' on %%s", p.Discussion.Title)
	},
	Fork: func(ua *UserActivity) string {
		var p PayloadFork
		p.GetData(ua.Payload)
		return fmt.Sprintf("Forked %%s to %s", p.Forkee.FullName)
	},
	Gollum: func(ua *UserActivity) string {
		var p PayloadGollum
		p.GetData(ua.Payload)

		if len(p.Pages) == 0 {
			return "Updated the wiki on %s"
		}

		// Example output: "Created wiki page 'Home' on repo-name"
		page := p.Pages[0]
		return fmt.Sprintf("%s wiki page '%s' on %%s", capitalize(page.Action), page.Title)
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
		var p PayloadIssues
		p.GetData(ua.Payload)
		return fmt.Sprintf("%s issue #%d ('%s') on %%s", capitalize(p.Action), p.Issue.Number, p.Issue.Title)
	},
	Member: func(ua *UserActivity) string {
		var p PayloadMember
		p.GetData(ua.Payload)
		return fmt.Sprintf("%s member '%s' to %%s", capitalize(p.Action), p.Member.Login)
	},
	Public: func(ua *UserActivity) string {
		return "Made %%s public"
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
		var p PayloadPullRequestReview
		p.GetData(ua.Payload)
		return fmt.Sprintf("%s a review for pull request #%d on %%s", capitalize(p.Action), p.PullRequest.Number)
	},
	PullRequestReviewComment: func(ua *UserActivity) string {
		var p PayloadPullRequestReviewComment
		p.GetData(ua.Payload)
		return fmt.Sprintf("%s a review comment on pull request #%d for %%s", capitalize(p.Action), p.PullRequest.Number)
	},
	Push: func(ua *UserActivity) string {
		var p PayloadPush
		p.GetData(ua.Payload)
		// Extract branch name from refs/heads/branch
		branch := p.Ref
		if strings.HasPrefix(p.Ref, "refs/heads/") {
			branch = p.Ref[11:]
		}
		return fmt.Sprintf("Pushed to %s at %%s", branch)
	},
	Release: func(ua *UserActivity) string {
		var p PayloadRelease
		p.GetData(ua.Payload)
		return fmt.Sprintf("%s release '%s' (%s) on %%s", capitalize(p.Action), p.Release.Name, p.Release.TagName)
	},
	Watch: func(ua *UserActivity) string {
		return "Starred %%s"
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
	if len(word) < 1 {
		return word // when "", return ""
	}
	return strings.ToUpper(string(word[0])) + string(word[1:])
}
