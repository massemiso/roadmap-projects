package main

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
