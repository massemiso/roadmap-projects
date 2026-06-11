package main

import (
	"encoding/json"
	"os"
)

const (
	FILE_PERM = 0o644
)

type TaskRepository struct {
	fileName string
}

func NewTaskRepository(fileName string) *TaskRepository {
	return &TaskRepository{
		fileName: fileName,
	}
}

func (r *TaskRepository) LoadTasks() ([]Task, error) {
	// read file
	tasks_enc, err := os.ReadFile(r.fileName)
	if err != nil {
		if os.IsNotExist(err) {
			return []Task{}, nil
		}
		return nil, err
	}

	// decoded json data
	var tasks_dec []Task
	err = json.Unmarshal(tasks_enc, &tasks_dec)
	if err != nil {
		return nil, err
	}
	return tasks_dec, nil
}

func (r *TaskRepository) SaveTasks(tasks []Task) error {
	// encode data to json
	tasks_enc, err := json.Marshal(tasks)
	if err != nil {
		return err
	}

	// write file with json data
	err = os.WriteFile(r.fileName, tasks_enc, FILE_PERM)
	if err != nil {
		return err
	}
	return nil
}
