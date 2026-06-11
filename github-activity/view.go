package main

import (
	"fmt"
	"strings"
	"time"
)

type View struct {
	Reset     string
	Bold      string
	Underline string
	Strike    string
	Italic    string
	Red       string
	Green     string
	Yellow    string
	Blue      string
	Purple    string
	Cyan      string
	White     string
}

func NewView(noColor bool) View {
	// if noColor exists and is true, set all output colorless
	if noColor {
		return View{} // all will be "" by default
	}

	return View{
		Reset:     "\033[0m",
		Bold:      "\033[1m",
		Underline: "\033[4m",
		Strike:    "\033[9m",
		Italic:    "\033[3m",
		Red:       "\033[31m",
		Green:     "\033[32m",
		Yellow:    "\033[33m",
		Blue:      "\033[34m",
		Purple:    "\033[35m",
		Cyan:      "\033[36m",
		White:     "\033[37m",
	}
}

func (v *View) FormatError(errorMsg string) string {
	return fmt.Sprintf("%s%s%s", v.Red, errorMsg, v.Reset)
}

func (v *View) FormatWarn(warnMsg string) string {
	return fmt.Sprintf("%s%s%s", v.Bold, warnMsg, v.Reset)
}

func (v *View) FormatTemplate(msg string, repoName string, date time.Time) string {
	// "color_date date color msg color_reset "
	out := "%s(%s)%s %s%s"
	outDate := date.Format(time.DateTime)
	msg = fmt.Sprintf(msg, repoName)
	return fmt.Sprintf(out, v.Green, outDate, (v.Reset + v.Cyan), msg, v.Reset)
}

func (v *View) FormatActivities(username string, activities []UserActivity) string {
	var sb strings.Builder

	title := v.FormatWarn(fmt.Sprintf("Recent activity of %s", username))
	sb.WriteString(title)

	for _, activity := range activities {
		template, repoName, date := activity.GetInfo()
		sb.WriteString(v.Bold)
		sb.WriteString("\n+ ")
		sb.WriteString(v.FormatTemplate(template, repoName, date))
	}
	return sb.String()
}
