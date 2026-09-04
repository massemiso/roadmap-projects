package tmdb

import (
	"net/http"
	"net/http/httptest"
	"testing"
)

type mockCacheService struct{}

func (m *mockCacheService) CheckCache() ([]byte, error) {
	return nil, nil
}

func (m *mockCacheService) SaveCache(body []byte) error {
	return nil
}

func TestFetchMovies(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		w.Write([]byte(`{
			"page": 1,
			"results": [
				{
					"title": "Test Movie",
					"overview": "Test Overview",
					"popularity": 10.0,
					"release_date": "2026-01-01",
					"vote_average": 8.0
				}
			]
		}`))
	}))
	defer server.Close()

	service := NewTMDBService(&mockCacheService{})
	service.BaseURL = server.URL

	movies, err := service.FetchMovies("popular", "en")
	if err != nil {
		t.Fatalf("Expected no error, got %v", err)
	}

	if len(movies) != 1 {
		t.Fatalf("Expected 1 movie, got %d", len(movies))
	}

	movie := movies[0]
	if movie.Title != "Test Movie" {
		t.Errorf("Expected title 'Test Movie', got %s", movie.Title)
	}
	if movie.VoteAverage != 8.0 {
		t.Errorf("Expected 8.0, got %f", movie.VoteAverage)
	}
}

func TestFetchMovies_Forbidden(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusForbidden)
	}))
	defer server.Close()

	service := NewTMDBService(&mockCacheService{})
	service.BaseURL = server.URL

	_, err := service.FetchMovies("popular", "en")
	if err == nil || err.Error() != "Error: Forbidden" {
		t.Errorf("Expected Forbidden error, got %v", err)
	}
}
