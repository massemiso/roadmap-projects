package main

import (
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"net/http"
	"os"
	"path/filepath"
	"time"
)

type GitHubServiceInterface interface {
	GetUserActivity(username string, limit int) ([]UserActivity, error)
}

type GitHubService struct {
	Client   *http.Client
	BaseURL  string
	CacheDir string
}

func NewGitHubService() *GitHubService {
	const github_api = "https://api.github.com/users/%s/events"
	return &GitHubService{
		Client:   &http.Client{Timeout: time.Second * 2},
		BaseURL:  github_api,
		CacheDir: os.TempDir(),
	}
}

func (s *GitHubService) GetUserActivity(username string, limit int) ([]UserActivity, error) {
	url := fmt.Sprintf(s.BaseURL, username)
	cachePath := s.getCachePath(username)

	var body []byte
	var bodyErr error

	// Check cache
	if info, err := os.Stat(cachePath); err == nil {
		if time.Since(info.ModTime()) < 5*time.Minute {
			body, bodyErr = os.ReadFile(cachePath)
		}
	}

	// Fetch from network if not in cache
	if body == nil {
		body, bodyErr = s.conn(url, username)
		if bodyErr == nil {
			_ = os.WriteFile(cachePath, body, 0644)
		}
	}

	if bodyErr != nil {
		return nil, bodyErr
	}
	activity, parseErr := s.parse(body)
	if parseErr != nil {
		return nil, parseErr
	}

	if limit != -1 && limit <= len(activity) {
		return activity[0:limit], nil
	} else {
		return activity, nil
	}
}

func (s *GitHubService) getCachePath(username string) string {
	return filepath.Join(s.CacheDir, "github-activity-"+username+".json")
}

func (s *GitHubService) conn(url string, username string) ([]byte, error) {
	client := s.Client

	// make request with url
	req, err := http.NewRequest(http.MethodGet, url, nil)
	if err != nil {
		return nil, err
	}

	// add headers as Github API recommends
	req.Header.Add("Accept", "application/vnd.github+json")
	req.Header.Add("User-Agent", "GitHub-Activity-Cli-massemiso")

	// send request and capture response
	res, getErr := client.Do(req)
	if getErr != nil {
		return nil, getErr
	}

	if res.Body != nil {
		defer res.Body.Close()
	}

	// handle http error code
	switch res.StatusCode {
	case http.StatusForbidden:
		return nil, errors.New("Error: Forbidden")
	case http.StatusNotFound:
		return nil, errors.New("Error: User " + username + " not found")
	case http.StatusServiceUnavailable:
		return nil, errors.New("Error: Service Unavailable")
	}

	if res.StatusCode != http.StatusOK {
		msg := fmt.Sprintf("Error: Something failed. Status Code: %d\n", res.StatusCode)
		return nil, errors.New(msg)
	}

	// read all the body of the response into []byte
	body, readErr := io.ReadAll(res.Body)
	if readErr != nil {
		return nil, readErr
	}

	return body, nil
}

func (s *GitHubService) parse(data []byte) ([]UserActivity, error) {
	activity := []UserActivity{}
	jsonErr := json.Unmarshal(data, &activity)
	if jsonErr != nil {
		return nil, jsonErr
	}
	return activity, nil
}
