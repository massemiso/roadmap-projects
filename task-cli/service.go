package main

import (
	"errors"
	"fmt"
	"slices"
)

type TaskService struct {
	repo *TaskRepository
}

func NewTaskService(r *TaskRepository) *TaskService {
	return &TaskService{
		repo: r,
	}
}

func (s *TaskService) Add(description string) error {
	tasks, err := s.repo.LoadTasks()
	if err != nil {
		fmt.Println("Something went wrong! Can't read file on disk")
		return err
	}

	newID := uint(1)
	if len(tasks) > 0 {
		newID = tasks[len(tasks)-1].ID + 1
	}
	newTask := NewTask(newID, description)

	err = s.repo.SaveTasks(append(tasks, newTask))
	if err != nil {
		fmt.Println("Something went wrong! Can't write file on disk")
	}

	fmt.Printf("Task created (ID : %d)\n", newTask.ID)
	return nil
}

func (s *TaskService) Update(id uint, newDesc string) error {
	tasks, err := s.repo.LoadTasks()
	if err != nil {
		fmt.Println("Something went wrong! Can't read file on disk")
		return err
	}

	idx, found := s.findIndexById(tasks, id)

	if !found {
		return errors.New("Task not found")
	}

	tasks[idx].SetDescription(newDesc)

	err = s.repo.SaveTasks(tasks)
	if err != nil {
		fmt.Println("Something went wrong! Can't write file on disk")
	}
	return nil
}

func (s *TaskService) Delete(id uint) error {
	tasks, err := s.repo.LoadTasks()
	if err != nil {
		fmt.Println("Something went wrong! Can't read file on disk")
		return err
	}
	_, found := s.findIndexById(tasks, id)

	if !found {
		return errors.New("Task not found")
	}

	tasks = slices.DeleteFunc(tasks, func(t Task) bool {
		return t.ID == id
	})

	err = s.repo.SaveTasks(tasks)
	if err != nil {
		fmt.Println("Something went wrong! Can't write file on disk")
	}

	return nil
}

func (s *TaskService) Mark(id uint, newStatus TaskStatus) error {
	tasks, err := s.repo.LoadTasks()
	if err != nil {
		fmt.Println("Something went wrong! Can't read file on disk")
		return err
	}
	idx, found := s.findIndexById(tasks, id)

	if !found {
		return errors.New("Task not found")
	}

	tasks[idx].SetStatus(newStatus)

	err = s.repo.SaveTasks(tasks)
	if err != nil {
		fmt.Println("Something went wrong! Can't write file on disk")
	}
	return nil
}

func (s *TaskService) GetAll() []string {
	tasks, err := s.repo.LoadTasks()
	if err != nil {
		return []string{}
	}
	out := []string{}
	for _, task := range tasks {
		out = append(out, task.ToString())
	}
	return out
}

func (s *TaskService) GetAllByMark(status TaskStatus) []string {
	tasks, err := s.repo.LoadTasks()
	if err != nil {
		return []string{}
	}
	out := []string{}
	for _, task := range tasks {
		if task.Status == status {
			out = append(out, task.ToString())
		}
	}
	return out
}

func (s *TaskService) findIndexById(tasks []Task, id uint) (int, bool) {
	idx := -1
	for i := range len(tasks) {
		if tasks[i].ID == id {
			idx = i
			break
		}
	}
	if idx == -1 {
		return 0, false
	}
	return idx, true
}
