package main

import (
	"encoding/json"
	"os"
	"path/filepath"
	"testing"
	"time"
)

const (
	TEMP_FILE = "data_temp.json"
	TEMP_PERM = 0o644
)

func create_file(tempFile string, data []byte) {
	os.WriteFile(tempFile, data, TEMP_PERM)
}

func TestNewTaskRepository(t *testing.T) {
	got := NewTaskRepository(TEMP_FILE)

	if got.fileName != TEMP_FILE {
		t.Errorf("Wrong filename!! want = '%s', got = '%s'", TEMP_FILE, got.fileName)
	}
}

func TestLoadTasks(t *testing.T) {
	tempDir := t.TempDir()
	tempFile := filepath.Join(tempDir, TEMP_FILE)

	// mock some file
	mock_tasks := []Task{
		{ID: 1, Description: "dentist", Status: TODO, CreatedAt: time.Now(), UpdatedAt: time.Now()},
		{ID: 2, Description: "church", Status: TODO, CreatedAt: time.Now(), UpdatedAt: time.Now()},
	}
	tasks_enc, _ := json.Marshal(mock_tasks)
	create_file(tempFile, tasks_enc)

	// test begins here
	repo := TaskRepository{tempFile}
	got, err := repo.LoadTasks()
	if err != nil {
		t.Errorf("Something went wrong!! want err = 'nil', got err = %v", err)
	}

	if len(got) != 2 {
		t.Errorf("Too many tasks!! want = %d tasks, got = %d tasks", len(mock_tasks), len(got))
	}

	for i, task := range got {
		if task.ID != mock_tasks[i].ID {
			t.Errorf("ID Integrity compromised!! want = %d, got = %d", mock_tasks[i].ID, task.ID)
		}
	}
}

func TestLoadTasks_FileDoesntExist(t *testing.T) {
	tempDir := t.TempDir()
	tempFile := filepath.Join(tempDir, TEMP_FILE)

	repo := TaskRepository{tempFile}
	got, err := repo.LoadTasks()

	if err != nil {
		t.Errorf("Something went wrong!! Want nil, got %v instead", err)
	}

	if len(got) != 0 {
		t.Errorf("Should return empty slice!! want = '0', got = %d", len(got))
	}
}

func TestLoadTasks_CorruptedJson(t *testing.T) {
	// mock some corrupted data (not in scope)
	tempDir := t.TempDir()
	tempFile := filepath.Join(tempDir, TEMP_FILE)
	create_file(tempFile, []byte("HELLO WORLD"))

	repo := TaskRepository{tempFile}
	got, err := repo.LoadTasks()

	if err == nil {
		t.Errorf("Something went wrong!! Want some error, got nil instead")
	}

	if got != nil {
		t.Errorf("Should return nil!! want = 'nil', got = %v", got)
	}
}

func TestSaveTasks(t *testing.T) {
	tempDir := t.TempDir()
	tempFile := filepath.Join(tempDir, TEMP_FILE)

	// mock some tasks
	mock_tasks := []Task{
		{ID: 1, Description: "dentist", Status: TODO, CreatedAt: time.Now(), UpdatedAt: time.Now()},
		{ID: 2, Description: "church", Status: TODO, CreatedAt: time.Now(), UpdatedAt: time.Now()},
	}

	// test begins here
	repo := TaskRepository{tempFile}
	err := repo.SaveTasks(mock_tasks)
	if err != nil {
		t.Errorf("Something went wrong!! want err = nil , got err = %v", err)
	}

	_, fileErr := os.Stat(tempFile)
	if os.IsNotExist(fileErr) {
		t.Errorf("%s file is not created", tempFile)
	}
}
