package tmdb

import (
	"encoding/csv"
	"encoding/json"
	"os"
	"path/filepath"
)

type ExportData interface {
	ToCsv() []string
}

func ExportJson[T ExportData](fileName string, data []T) error {
	// marshal repos
	json, err := json.Marshal(data)
	if err != nil {
		return err
	}

	// write json file
	path := filepath.Join(fileName + ".json")
	err = os.WriteFile(path, json, 0o644)
	if err != nil {
		return err
	}

	return nil
}

func ExportCsv[T ExportData](fileName string, headers []string, data []T) error {
	// create csv file
	file, err := os.Create(fileName + ".csv")
	if err != nil {
		return err
	}
	defer file.Close()

	// parse repos to csv format
	csvData := [][]string{
		headers,
	}
	for _, object := range data {
		csvData = append(csvData, object.ToCsv())
	}

	// write csv file
	writer := csv.NewWriter(file)
	defer writer.Flush()

	err = writer.WriteAll(csvData)
	if err != nil {
		return err
	}

	return nil
}
