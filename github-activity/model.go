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

var eventTemplates = map[Event]string{
	CommitComment:            "Made a commit comment on %s",
	Create:                   "Created a branch/tag on %s",
	Delete:                   "Deleted a branch/tag on %s",
	Discussion:               "Started a discussion on %s",
	Fork:                     "Forked %s",
	Gollum:                   "Created/Updated a wiki page on %s",
	IssueComment:             "Made an activity in an issue/pull request comment on %s",
	Issues:                   "Made an activity in an issue on %s",
	Member:                   "Made an activity in a collaboration on %s",
	Public:                   "Updated %s to public",
	PullRequest:              "Made an activity in a pull request on %s",
	PullRequestReview:        "Made an activity in a pull request review on %s",
	PullRequestReviewComment: "Made an activity in a pull request review comment on %s",
	Push:                     "Pushed commit to %s",
	Release:                  "Made an activity related to a release on %s",
	Watch:                    "Starred %s",
}

type UserActivity struct {
	ID        string    `json:"id"`
	Type      Event     `json:"type"`
	Actor     User      `json:"actor"`
	Repo      Repo      `json:"repo"`
	Payload   Payload   `json:"payload"`
	Public    bool      `json:"public"`
	CreatedAt time.Time `json:"created_at"`
}

func (ua *UserActivity) GetInfo() (string, string, time.Time) {
	template, ok := eventTemplates[ua.Type]
	if !ok {
		template = "Performed an unknown action (" + string(ua.Type) + ") on %s"
	}
	return template, ua.Repo.Name, ua.CreatedAt
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
