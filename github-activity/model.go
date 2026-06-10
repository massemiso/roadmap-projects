package main

import (
	"time"
)

type Event string

const (
	CommitComment            Event = "CommitCommentEvent"
	Create                   Event = "CreateEvent"
	Delete                   Event = "DeleteEvent"
	Discussion               Event = "DiscussionEvent"
	Fork                     Event = "ForkEvent"
	Gollum                   Event = "GollumEvent"
	IssueComment             Event = "IssueCommentEvent"
	Issues                   Event = "IssuesEvent"
	Member                   Event = "MemberEvent"
	Public                   Event = "PublicEvent"
	PullRequest              Event = "PullRequestEvent"
	PullRequestReview        Event = "PullRequestReviewEvent"
	PullRequestReviewComment Event = "PullRequestReviewCommentEvent"
	Push                     Event = "PushEvent"
	Release                  Event = "ReleaseEvent"
	Watch                    Event = "WatchEvent"
)

type UserActivity struct {
	ID        string    `json:"id"`
	Type      Event     `json:"type"`
	Actor     User      `json:"actor"`
	Repo      Repo      `json:"repo"`
	Payload   Payload   `json:"payload"`
	Public    bool      `json:"public"`
	CreatedAt time.Time `json:"created_at"`
}

func (ua *UserActivity) GetInfo() string {
	var msg string

	switch ua.Type {
	case CommitComment:
		msg = "Made a commit comment on %s"
	case Create:
		msg = "Created a branch/tag on %s"
	case Delete:
		msg = "Deleted a branch/tag on %s"
	case Discussion:
		msg = "Started a discussion on %s"
	case Fork:
		msg = "Forked %s"
	case Gollum:
		msg = "Created/Updated a wiki page on %s"
	case IssueComment:
		msg = "Made an activity in an issue/pull request comment on %s"
	case Issues:
		msg = "Made an activity in an issue on %s"
	case Member:
		msg = "Made an activity in a collaboration on %s"
	case Public:
		msg = "Updated %s to public"
	case PullRequest:
		msg = "Made an activity in a pull request on %s"
	case PullRequestReview:
		msg = "Made an activity in a pull request review on %s"
	case PullRequestReviewComment:
		msg = "Made an activity in a pull request review comment on %s"
	case Push:
		msg = "Pushed commit to %s"
	case Release:
		msg = "Made an activity related to a release on %s"
	case Watch:
		msg = "Starred %s"
	}

	view := View{}
	return view.FormatInfo(msg, ua.Repo.Name, ua.CreatedAt)
}

type User struct {
	ID           int    `json:"id"`
	Login        string `json:"login"`
	DisplayLogin string `json:"display_login"`
	GRAvatarID   string `json:"gravatar_id"`
	URL          string `json:"url"`
	AvatarURL    string `json:"avatar_url"`
}

type Repo struct {
	ID   int    `json:"id"`
	Name string `json:"name"`
	URL  string `json:"url"`
}

type Payload struct {
	RepositoryID int    `json:"repository_id"`
	PushID       int    `json:"push_id"`
	Ref          string `json:"ref"`
	Head         string `json:"head"`
	Before       string `json:"before"`
}
