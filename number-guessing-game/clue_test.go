package main

import "testing"

func TestRangeHint(t *testing.T) {
	tests := []struct {
		name     string
		attempts uint8
		secret   uint8
		want     string
	}{
		{
			name:     "early game, n=50, attempts=1",
			attempts: 1,
			secret:   50,
			want:     "The number is between 25 and 75", // 50 +/- 25
		},
		{
			name:     "late game, n=50, attempts=5",
			attempts: 5,
			secret:   50,
			want:     "The number is between 45 and 55", // 50 +/- 5
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			rangeFunc := rangeHint(tt.attempts)
			actual := rangeFunc(tt.secret)
			if actual != tt.want {
				t.Errorf("ERROR! rangeHint() = %s, expected = %s", actual, tt.want)
			}
		})
	}
}

func TestParityHint(t *testing.T) {
	tests := []struct {
		name   string
		secret uint8
		want   string
	}{
		{
			name:   "odd",
			secret: 3,
			want:   "The number is Odd",
		},
		{
			name:   "even",
			secret: 4,
			want:   "The number is Even",
		},
		{
			name:   "min value 1",
			secret: 1,
			want:   "The number is Odd",
		},
		{
			name:   "max value 100",
			secret: 100,
			want:   "The number is Even",
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			parityFunc := parityHint()
			actual := parityFunc(tt.secret)
			if actual != tt.want {
				t.Errorf("ERROR! parityHint() = %s, expected = %s", actual, tt.want)
			}
		})
	}
}

func TestDigitsHint(t *testing.T) {
	tests := []struct {
		name   string
		secret uint8
		want   string
	}{
		{
			name:   "one digit, n=5",
			secret: 5,
			want:   "The number has one digit",
		},
		{
			name:   "two digits max value, n=100",
			secret: 100,
			want:   "The number has two equal digits",
		},
		{
			name:   "divisible by 10, n=50",
			secret: 50,
			want:   "The number has a 0 in it",
		},
		{
			name:   "same number in digits, n=33",
			secret: 33,
			want:   "The number has two equal digits",
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			digitsFunc := digitsHint()
			actual := digitsFunc(tt.secret)
			if actual != tt.want {
				t.Errorf("ERROR! digitsHint() = %s, expected = %s", actual, tt.want)
			}
		})
	}
}
