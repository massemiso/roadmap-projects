package github

import (
	"os"
	"path/filepath"
	"time"
)

type CacheServiceInterface interface {
	CheckCache() ([]byte, error)
	SaveCache(body []byte) error
}

type CacheService struct {
	Dir  string
	File string
}

func NewCacheService(fileName string) *CacheService {
	directory := os.TempDir()
	return &CacheService{
		Dir:  directory,
		File: filepath.Join(directory, fileName+".json"),
	}
}

func (s *CacheService) CheckCache() ([]byte, error) {
	info, err := os.Stat(s.File)
	if err != nil {
		return nil, err
	}

	if time.Since(info.ModTime()) > 5*time.Minute {
		return nil, nil
	}

	body, bodyErr := os.ReadFile(s.File)
	if bodyErr != nil {
		return nil, bodyErr
	}

	return body, nil
}

func (s *CacheService) SaveCache(body []byte) error {
	return os.WriteFile(s.File, body, 0o644)
}
