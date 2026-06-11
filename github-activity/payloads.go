package main

import (
	"encoding/json"
	"time"
)

type UserActivity struct {
	ID        string          `json:"id"`
	Type      Event           `json:"type"`
	Actor     User            `json:"actor"`
	Repo      Repo            `json:"repo"`
	Payload   json.RawMessage `json:"payload"`
	Public    bool            `json:"public"`
	CreatedAt time.Time       `json:"created_at"`
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

type Payload interface {
	GetData(data []byte)
}

type PayloadCommitComment struct {
	Action  string `json:"action"`
	Comment struct {
		Body string `json:"body"`
		URL  string `json:"html_url"`
	} `json:"comment"`
}

func (p *PayloadCommitComment) GetData(data []byte) {
	json.Unmarshal(data, &p)
}

type PayloadCreate struct {
	Description string `json:"description"`
}

func (p *PayloadCreate) GetData(data []byte) {
	json.Unmarshal(data, &p)
}

type PayloadIssueComment struct {
	Action string `json:"action"`
	Issue  struct {
		Number int `json:"number"`
	} `json:"issue"`
	Comment struct {
		ID      int    `json:"id"`
		HtmlURL string `json:"html_url"`
		Body    string `json:"body"`
	} `json:"comment"`
}

func (p *PayloadIssueComment) GetData(data []byte) {
	json.Unmarshal(data, &p)
}

type PayloadDelete struct {
	Ref        string `json:"ref"`
	RefType    string `json:"ref_type"`
	PusherType string `json:"pusher_type"`
}

func (p *PayloadDelete) GetData(data []byte) {
	json.Unmarshal(data, &p)
}

type PayloadDiscussion struct {
	Action     string `json:"action"`
	Discussion struct {
		Title   string `json:"title"`
		HTMLURL string `json:"html_url"`
		Number  int    `json:"number"`
	} `json:"discussion"`
}

func (p *PayloadDiscussion) GetData(data []byte) {
	json.Unmarshal(data, &p)
}

type PayloadFork struct {
	Action string `json:"action"`
	Forkee struct {
		FullName string `json:"full_name"` // e.g., "your-user/original-repo"
		HTMLURL  string `json:"html_url"`
	} `json:"forkee"`
}

func (p *PayloadFork) GetData(data []byte) {
	json.Unmarshal(data, &p)
}

type PayloadGollum struct {
	Pages []struct {
		Title  string `json:"title"`
		Action string `json:"action"`
		URL    string `json:"html_url"`
	} `json:"pages"`
}

func (p *PayloadGollum) GetData(data []byte) {
	json.Unmarshal(data, &p)
}

type PayloadPullRequest struct {
	Action string `json:"action"`
	Number int    `json:"number"`
}

func (p *PayloadPullRequest) GetData(data []byte) {
	json.Unmarshal(data, &p)
}

type PayloadIssues struct {
	Action string `json:"action"`
	Issue  struct {
		Number int    `json:"number"`
		Title  string `json:"title"`
	} `json:"issue"`
}

func (p *PayloadIssues) GetData(data []byte) {
	json.Unmarshal(data, &p)
}

type PayloadMember struct {
	Action string `json:"action"`
	Member struct {
		Login string `json:"login"`
	} `json:"member"`
}

func (p *PayloadMember) GetData(data []byte) {
	json.Unmarshal(data, &p)
}

type PayloadPublic struct{}

func (p *PayloadPublic) GetData(data []byte) {
	json.Unmarshal(data, &p)
}

type PayloadPullRequestReview struct {
	Action      string `json:"action"`
	PullRequest struct {
		Number int `json:"number"`
	} `json:"pull_request"`
}

func (p *PayloadPullRequestReview) GetData(data []byte) {
	json.Unmarshal(data, &p)
}

type PayloadPullRequestReviewComment struct {
	Action      string `json:"action"`
	PullRequest struct {
		Number int `json:"number"`
	} `json:"pull_request"`
	Comment struct {
		ID int `json:"id"`
	} `json:"comment"`
}

func (p *PayloadPullRequestReviewComment) GetData(data []byte) {
	json.Unmarshal(data, &p)
}

type PayloadPush struct {
	Ref string `json:"ref"`
}

func (p *PayloadPush) GetData(data []byte) {
	json.Unmarshal(data, &p)
}

type PayloadRelease struct {
	Action  string `json:"action"`
	Release struct {
		TagName string `json:"tag_name"`
		Name    string `json:"name"`
	} `json:"release"`
}

func (p *PayloadRelease) GetData(data []byte) {
	json.Unmarshal(data, &p)
}

type PayloadWatch struct {
	Action string `json:"action"`
}

func (p *PayloadWatch) GetData(data []byte) {
	json.Unmarshal(data, &p)
}
