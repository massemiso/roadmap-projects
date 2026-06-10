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

type PayloadPullRequest struct {
	Action string `json:"action"`
	Number int    `json:"number"`
}

func (p *PayloadPullRequest) GetData(data []byte) {
	json.Unmarshal(data, &p)
}
