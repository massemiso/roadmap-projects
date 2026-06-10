package main

import (
	"fmt"
	"time"
)

type TaskStatus uint

const (
	TODO TaskStatus = iota
	IN_PROGRESS
	DONE
)

type Task struct {
	ID          uint       `json:"id"`
	Description string     `json:"description"`
	Status      TaskStatus `json:"status"`
	CreatedAt   time.Time  `json:"createdAt"`
	UpdatedAt   time.Time  `json:"updatedAt"`
}

func NewTask(id uint, description string) Task {
	return Task{
		ID:          id,
		Description: description,
		Status:      TODO,
		CreatedAt:   time.Now(),
		UpdatedAt:   time.Now(),
	}
}

func (t *Task) SetDescription(description string) {
	if description != "" {
		t.Description = description
		t.UpdatedAt = time.Now()
	}
}

func (t *Task) SetStatus(newStatus TaskStatus) {
	t.Status = newStatus
	t.UpdatedAt = time.Now()
}

func (t *Task) ToString() string {
	var status string
	switch t.Status {
	case TODO:
		status = "TODO"
	case IN_PROGRESS:
		status = "IN_PROGRESS"
	case DONE:
		status = "DONE"
	default:
		fmt.Println("sussy")
	}
	return fmt.Sprintf("%d - %s - %s", t.ID, t.Description, status)
}
