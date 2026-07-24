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
	FetchMovies(typeVar string) ([]Movie, error)
}

type TMDBService struct {
	Client *http.Client
	ApiKey string
}

func NewTMDBService() *TMDBService {
	api_key := os.Getenv("TMDB_API_KEY")
	return &TMDBService{
		Client: &http.Client{Timeout: time.Second * 10},
		ApiKey: api_key,
	}
}

func (s *TMDBService) FetchMovies(typeVar string) ([]Movie, error) {
	url := s.makeQuery(typeVar)

	body, bodyErr := s.conn(url)
	if bodyErr != nil {
		return nil, bodyErr
	}

	movies, parseErr := s.parse(body)
	if parseErr != nil {
		return nil, parseErr
	}

	return movies, nil
}

func (s *TMDBService) makeQuery(typeVar string) string {
	queries := map[string]string{
		"playing":  "https://api.themoviedb.org/3/movie/now_playing",
		"popular":  "https://api.themoviedb.org/3/movie/popular",
		"top":      "https://api.themoviedb.org/3/movie/top_rated",
		"upcoming": "https://api.themoviedb.org/3/movie/upcoming",
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
