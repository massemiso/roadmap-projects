package github

import "fmt"

type TrendingRepo struct {
	FullName    string `json:"full_name"`
	Description string `json:"description"`
	Stars       int    `json:"watchers_count"`
	Language    string `json:"language"`
}

type SearchResponse struct {
	TotalCount int            `json:"total_count"`
	Items      []TrendingRepo `json:"items"`
}

func (r *TrendingRepo) ToString() string {
	return fmt.Sprintf("TrendingRepo {FullName=%s, Description=%s, Stars=%d, Language=%s}",
		r.FullName, r.Description, r.Stars, r.Language)
}
