package github

import (
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"net/http"
	"time"
)

type GitHubServiceInterface interface {
	GetTrendingRepos(duration string, limit uint) ([]TrendingRepo, error)
	ExportTrendingRepos(repos []TrendingRepo, format string) error
}

type GitHubService struct {
	Client  *http.Client
	BaseURL string
	Cache   CacheServiceInterface
}

func NewGitHubService(cache CacheServiceInterface) *GitHubService {
	const github_api = "https://api.github.com/search/repositories?q=%s"
	return &GitHubService{
		Client:  &http.Client{Timeout: time.Second * 10},
		BaseURL: github_api,
		Cache:   cache,
	}
}

func (s *GitHubService) GetTrendingRepos(duration string, limit uint) ([]TrendingRepo, error) {
	url := s.makeQuery(duration, limit)
	body, bodyErr := s.Cache.CheckCache()

	// Fetch from network if not in cache
	if body == nil {
		body, bodyErr = s.conn(url)
		if bodyErr == nil {
			bodyErr = s.Cache.SaveCache(body)
		}
	}

	if bodyErr != nil {
		return nil, bodyErr
	}

	repos, parseErr := s.parse(body)
	if parseErr != nil {
		return nil, parseErr
	}

	return repos, nil
}

func (s *GitHubService) makeQuery(duration string, limit uint) string {
	var date string
	format := "2006-01-02"

	// assume duration is a valid duration
	var diff time.Duration
	switch duration {
	case "day":
		diff = 24 * time.Hour
	case "week":
		diff = 24 * time.Hour * 7
	case "month":
		diff = 24 * time.Hour * 31
	case "year":
		diff = 24 * time.Hour * 365
	}

	date = time.Now().Add(-diff).Format(format)
	query := fmt.Sprintf("created:>%s&sort=stars&order=desc&per_page=%d", date, limit)
	url := fmt.Sprintf(s.BaseURL, query)
	return url
}

func (s *GitHubService) conn(url string) ([]byte, error) {
	client := s.Client

	// make request
	req, err := http.NewRequest(http.MethodGet, url, nil)
	if err != nil {
		return nil, err
	}

	// requested headers
	req.Header.Add("Accept", "application/vnd.github+json")
	req.Header.Add("User-Agent", "GitHub-Activity-Cli-massemiso")

	// send request and capture response
	res, resErr := client.Do(req)
	if resErr != nil {
		return nil, resErr
	}

	if res.Body != nil {
		defer res.Body.Close()
	}

	// handle possible http status codes
	switch res.StatusCode {
	case http.StatusForbidden:
		return nil, errors.New("Error: Forbidden")
	case http.StatusNotModified:
		return nil, errors.New("Error: Not modified")
	case http.StatusUnprocessableEntity:
		return nil, errors.New("Error: Validation failed, or the endpoint has been spammed.")
	case http.StatusServiceUnavailable:
		return nil, errors.New("Error: Service Unavailable")
	}
	if res.StatusCode != http.StatusOK {
		msg := fmt.Sprintf("Error: Something failed. Status code: %d", res.StatusCode)
		return nil, errors.New(msg)
	}

	// read & return body
	body, readErr := io.ReadAll(res.Body)
	if readErr != nil {
		return nil, readErr
	}

	return body, nil
}

func (s *GitHubService) parse(body []byte) ([]TrendingRepo, error) {
	var searchResult SearchResponse
	jsonErr := json.Unmarshal(body, &searchResult)
	if jsonErr != nil {
		return nil, jsonErr
	}
	return searchResult.Items, nil
}

func (s *GitHubService) ExportTrendingRepos(repos []TrendingRepo, format string) error {
	switch format {
	case "json":
		return ExportJson("trending", repos)
	case "csv":
		headers := []string{"FULL_NAME", "DESCRIPTION", "STARS", "LANGUAGE"}
		return ExportCsv("trending", headers, repos)
	default:
		return errors.New("export file format invalid")
	}
}
