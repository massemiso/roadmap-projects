package ui

type Colors struct {
	Reset     string
	Red       string
	Green     string
	Yellow    string
	Blue      string
	Magenta   string
	Cyan      string
	Gray      string
	White     string
	Bold      string
	Underline string
}

func NewColors(noColor bool) Colors {
	if noColor {
		return Colors{}
	}
	return Colors{
		Reset:     "\033[0m",
		Red:       "\033[31m",
		Green:     "\033[32m",
		Yellow:    "\033[33m",
		Blue:      "\033[34m",
		Magenta:   "\033[35m",
		Cyan:      "\033[36m",
		Gray:      "\033[37m",
		White:     "\033[97m",
		Bold:      "\033[1m",
		Underline: "\033[4m",
	}
}
