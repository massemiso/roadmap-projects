package github

import (
	"encoding/csv"
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
	GetTrendingRepos(duration string, limit uint) ([]TrendingRepo, error)
	ExportTrendingRepos(repos []TrendingRepo, format string) error
}

type GitHubService struct {
	Client   *http.Client
	BaseURL  string
	CacheDir string
}

func NewGitHubService() *GitHubService {
	const github_api = "https://api.github.com/search/repositories?q=%s"
	return &GitHubService{
		Client:   &http.Client{Timeout: time.Second * 10},
		BaseURL:  github_api,
		CacheDir: os.TempDir(),
	}
}

func (s *GitHubService) GetTrendingRepos(duration string, limit uint) ([]TrendingRepo, error) {
	url := s.makeQuery(duration, limit)
	cachePath := s.getCachePath(duration, limit)

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
		body, bodyErr = s.conn(url)
		if bodyErr == nil {
			_ = os.WriteFile(cachePath, body, 0o644)
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

func (s *GitHubService) getCachePath(duration string, limit uint) string {
	return filepath.Join(s.CacheDir, "github-trending-"+
		duration+
		fmt.Sprintf("%d", limit)+
		".json")
}

func (s *GitHubService) ExportTrendingRepos(repos []TrendingRepo, format string) error {
	switch format {
	case "json":
		return s.exportJson(repos)
	case "csv":
		return s.exportCsv(repos)
	default:
		return errors.New("export file format invalid")
	}
}

func (s *GitHubService) exportJson(repos []TrendingRepo) error {
	// marshal repos
	json, err := json.Marshal(repos)
	if err != nil {
		return err
	}

	// write json file
	path := filepath.Join("trending.json")
	err = os.WriteFile(path, json, 0o644)
	if err != nil {
		return err
	}

	return nil
}

func (s *GitHubService) exportCsv(repos []TrendingRepo) error {
	// create csv file
	file, err := os.Create("trending.csv")
	if err != nil {
		return err
	}
	defer file.Close()

	// parse repos to csv format
	data := [][]string{
		{"FULL_NAME", "DESCRIPTION", "STARS", "LANGUAGE"},
	}
	for _, repo := range repos {
		data = append(data, []string{
			repo.FullName,
			repo.Description,
			fmt.Sprintf("%d", repo.Stars),
			repo.Language,
		})
	}

	// write csv file
	writer := csv.NewWriter(file)
	defer writer.Flush()

	err = writer.WriteAll(data)
	if err != nil {
		return err
	}

	return nil
}
