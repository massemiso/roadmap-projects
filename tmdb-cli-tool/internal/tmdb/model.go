package tmdb

import (
	"fmt"
)

type Movie struct {
	Title       string  `json:"title"`
	Overview    string  `json:"overview"`
	Popularity  float64 `json:"popularity"`
	ReleaseDate string  `json:"release_date"`
	VoteAverage float64 `json:"vote_average"`
}

type ApiResponse struct {
	Page    int     `json:"page"`
	Results []Movie `json:"results"`
}

func (m *Movie) ToString() string {
	return fmt.Sprintf("Movie {Title=%s, Overview=%s, Popularity=%f, ReleaseDate=%s, VoteAverage=%f}",
		m.Title, m.Overview, m.Popularity, m.ReleaseDate, m.VoteAverage)
}
