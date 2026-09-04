package tmdb

import (
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"net/http"
	"os"
	"time"
)

type TMDBServiceInterface interface {
	FetchMovies(typeVar string, lang string) ([]Movie, error)
	ExportMovies([]Movie, string) error
}

type TMDBService struct {
	Client  *http.Client
	ApiKey  string
	BaseURL string
	Cache   CacheServiceInterface
}

func NewTMDBService(cache CacheServiceInterface) *TMDBService {
	api_key := os.Getenv("TMDB_API_KEY")
	return &TMDBService{
		Client:  &http.Client{Timeout: time.Second * 10},
		ApiKey:  api_key,
		BaseURL: "https://api.themoviedb.org",
		Cache:   cache,
	}
}

func (s *TMDBService) FetchMovies(typeVar string, lang string) ([]Movie, error) {
	// check if available cache
	body, bodyErr := s.Cache.CheckCache()

	// if cache not available
	if body == nil {
		url := s.makeQuery(typeVar, lang)

		body, bodyErr = s.conn(url)
		if bodyErr != nil {
			return nil, bodyErr
		}

		// save new cache
		cacheErr := s.Cache.SaveCache(body)
		if cacheErr != nil {
			return nil, cacheErr
		}
	}

	if bodyErr != nil {
		return nil, bodyErr
	}

	movies, parseErr := s.parse(body)
	if parseErr != nil {
		return nil, parseErr
	}

	return movies, nil
}

func (s *TMDBService) makeQuery(typeVar string, lang string) string {
	partQuery := s.BaseURL + "/3/movie/%s?language=" + lang
	queries := map[string]string{
		"playing":  fmt.Sprintf(partQuery, "now_playing"),
		"popular":  fmt.Sprintf(partQuery, "popular"),
		"top":      fmt.Sprintf(partQuery, "top_rated"),
		"upcoming": fmt.Sprintf(partQuery, "upcoming"),
	}
	return queries[typeVar]
}

func (s *TMDBService) conn(url string) ([]byte, error) {
	client := s.Client

	// make request
	req, err := http.NewRequest(http.MethodGet, url, nil)
	if err != nil {
		return nil, err
	}

	// requested headers
	req.Header.Add("Authorization", fmt.Sprintf("Bearer %s", s.ApiKey))
	req.Header.Add("Accept", "application/json")

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

func (s *TMDBService) parse(body []byte) ([]Movie, error) {
	var apiResponse ApiResponse
	jsonErr := json.Unmarshal(body, &apiResponse)
	if jsonErr != nil {
		return nil, jsonErr
	}
	return apiResponse.Results, nil
}

func (s *TMDBService) ExportMovies(movies []Movie, format string) error {
	switch format {
	case "json":
		return ExportJson("movies", movies)
	case "csv":
		headers := []string{"TITLE", "OVERVIEW", "POPULARITY", "RELEASE_DATE", "VOTE_AVERAGE"}
		return ExportCsv("movies", headers, movies)
	default:
		return errors.New("export file format invalid")
	}
}
