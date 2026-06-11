package main

import "testing"

func TestCapitalize(t *testing.T) {
	word := "hello"
	want := "Hello"

	got := capitalize(word)

	if got != want {
		t.Errorf("Error!! want capitalize(%s)=%s, got=%s", word, want, got)
	}
}

func TestCapitalizeOneLetter(t *testing.T) {
	word := "a"
	want := "A"

	got := capitalize(word)

	if got != want {
		t.Errorf("Error!! want capitalize(%s)=%s, got=%s", word, want, got)
	}
}

func TestCapitalizeEmpty(t *testing.T) {
	word := ""
	want := ""

	got := capitalize(word)

	if got != want {
		t.Errorf("Error!! want capitalize(%s)=%s, got=%s", word, want, got)
	}
}

func TestCapitalizeAlreadyCapitalized(t *testing.T) {
	word := "Hello"
	want := "Hello"

	got := capitalize(word)

	if got != want {
		t.Errorf("Error!! want capitalize(%s)=%s, got=%s", word, want, got)
	}
}

func TestGetInfo(t *testing.T) {
}
