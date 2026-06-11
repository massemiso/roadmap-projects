package main

import (
	"encoding/json"
	"testing"
)

func TestCapitalize(t *testing.T) {
	word := "hello"
	want := "Hello"

	got := capitalize(word)

	if got != want {
		t.Errorf("Error!! want capitalize(%s)=%s, got=%s", word, want, got)
	}
}

func TestCapitalizeOneLetter(t *testing.T) {
	word := "a"
	want := "A"

	got := capitalize(word)

	if got != want {
		t.Errorf("Error!! want capitalize(%s)=%s, got=%s", word, want, got)
	}
}

func TestCapitalizeEmpty(t *testing.T) {
	word := ""
	want := ""

	got := capitalize(word)

	if got != want {
		t.Errorf("Error!! want capitalize(%s)=%s, got=%s", word, want, got)
	}
}

func TestCapitalizeAlreadyCapitalized(t *testing.T) {
	word := "Hello"
	want := "Hello"

	got := capitalize(word)

	if got != want {
		t.Errorf("Error!! want capitalize(%s)=%s, got=%s", word, want, got)
	}
}

func TestGetInfo(t *testing.T) {
	tests := []struct {
		name     string
		activity UserActivity
		want     string
	}{
		{
			name: "WatchEvent",
			activity: UserActivity{
				Type: Watch,
				Repo: Repo{Name: "user/repo"},
			},
			want: "Starred %s",
		},
		{
			name: "PushEvent",
			activity: UserActivity{
				Type:    Push,
				Repo:    Repo{Name: "user/repo"},
				Payload: json.RawMessage(`{"ref": "refs/heads/main"}`), // Define raw JSON here
			},
			want: "Pushed to main at %s",
		},
		{
			name: "PushEvent",
			activity: UserActivity{
				Type:    Push,
				Repo:    Repo{Name: "user/repo"},
				Payload: json.RawMessage(`{"ref": "man"}`), // Define raw JSON here
			},
			want: "Pushed to man at %s",
		},
		{
			name: "IssuesEvent",
			activity: UserActivity{
				Type: Issues,
				Repo: Repo{Name: "user/repo"},
				Payload: json.RawMessage(`{
				"action": "created",
				"issue": {
					"number": 100,
					"title": "A cool title"
				}}`),
			},
			want: "Created issue #100 ('A cool title') on %s",
		},
		{
			name: "GollumEvent",
			activity: UserActivity{
				Type: Gollum,
				Repo: Repo{Name: "user/repo"},
				Payload: json.RawMessage(`{
				"pages": [
				{
					"title": "A cool title",
					"action": "created",
					"html_url": "https://someurl.com/100"
				},
				{
					"title": "A cool title kk",
					"action": "updated",
					"html_url": "https://someurl.com/wiki/101"
				}
			]}`),
			},
			want: "Created wiki page 'A cool title' on %s",
		},
		{
			name: "GollumEvent",
			activity: UserActivity{
				Type:    Gollum,
				Repo:    Repo{Name: "user/repo"},
				Payload: json.RawMessage(`{"pages": []}`),
			},
			want: "Updated the wiki on %s",
		},
		{
			name: "Unknown",
			activity: UserActivity{
				Type: "UnknownEvent",
				Repo: Repo{Name: "user/repo"},
			},
			want: "Performed an unknown action (UnknownEvent) on %s",
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, _, _ := tt.activity.GetInfo()
			if got != tt.want {
				t.Errorf("GetInfo() got = \"%v\", want = \"%v\"", got, tt.want)
			}
		})
	}
}
