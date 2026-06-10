package main

import (
	"testing"
	"time"
)

func TestNewTask(t *testing.T) {
	id := uint(1)
	description := "my super duper description"
	now := time.Now()
	want := Task{
		ID:          id,
		Description: description,
		Status:      TODO,
		CreatedAt:   now,
		UpdatedAt:   now,
	}

	got := NewTask(id, description)

	if got.ID != want.ID {
		t.Errorf("NewTask(%d, %s).ID = '%d'; want '%d'", id, description, got.ID, want.ID)
	}
	if got.Description != want.Description {
		t.Errorf("NewTask(%d, %s).Description = '%s'; want '%s'", id, description, got.Description, want.Description)
	}
	if got.Status != want.Status {
		t.Errorf("NewTask(%d, %s).Status = '%d'; want '%d'", id, description, got.Status, want.Status)
	}
	if want.CreatedAt.IsZero() {
		t.Errorf("CreatedAt was not initialized")
	}
	if time.Since(want.CreatedAt) > time.Second {
		t.Errorf("CreatedAt is too old!")
	}
	if want.UpdatedAt.IsZero() {
		t.Errorf("UpdatedAt was not initialized")
	}
	if time.Since(want.UpdatedAt) > time.Second {
		t.Errorf("UpdatedAt is too old!")
	}
}

func TestSetDescription(t *testing.T) {
	before := time.Now()
	got := Task{
		Description: "something",
		UpdatedAt:   before,
	}

	// wait a tiny bit so we can compare
	time.Sleep(time.Millisecond)

	got.SetDescription("new")

	if got.Description != "new" {
		t.Errorf("task.SetDescription('new') = '%s'; want 'new'", got.Description)
	}

	if !got.UpdatedAt.After(before) {
		t.Errorf("UpdatedAt was not updated! Before: %v, After: %v", before, got.UpdatedAt)
	}

}

func TestSetDescriptionEmpty(t *testing.T) {
	before := time.Now()
	got := Task{
		Description: "something",
		UpdatedAt:   before,
	}

	// wait a tiny bit so we can compare
	time.Sleep(time.Millisecond)

	got.SetDescription("")

	if got.Description != "something" {
		t.Errorf("task.SetDescription('new') = '%s'; want 'something'", got.Description)
	}

	// if desc empty, task should not update anything
	if !got.UpdatedAt.Equal(before) {
		t.Errorf("UpdatedAt wasn't supposed to update! Before: %v, After: %v", before, got.UpdatedAt)
	}
}

func TestSetStatus(t *testing.T) {
	before := time.Now()
	got := Task{
		Status:    TODO,
		UpdatedAt: before,
	}

	// wait a tiny bit so we can compare
	time.Sleep(time.Millisecond)

	got.SetStatus(DONE)

	if got.Status != DONE {
		t.Errorf("task.SetStatus(DONE) = '%d'; want '%d'", got.Status, DONE)
	}

	if !got.UpdatedAt.After(before) {
		t.Errorf("UpdatedAt was not updated! Before: %v, After: %v", before, got.UpdatedAt)
	}
}
