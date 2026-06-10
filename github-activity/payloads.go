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

type PayloadCreate struct {
	Description string `json:"description"`
}

type PayloadPullRequest struct {
	Action string `json:"action"`
	Number int    `json:"number"`
}
