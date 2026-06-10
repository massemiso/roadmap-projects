package main

import (
	"fmt"
	"strings"
	"time"
)

const (
	Reset     = "\033[0m"
	Bold      = "\033[1m"
	Underline = "\033[4m"
	Strike    = "\033[9m"
	Italic    = "\033[3m"

	Red    = "\033[31m"
	Green  = "\033[32m"
	Yellow = "\033[33m"
	Blue   = "\033[34m"
	Purple = "\033[35m"
	Cyan   = "\033[36m"
	White  = "\033[37m"
)

type View struct{}

func (v *View) FormatUsage(binaryName string) string {
	return fmt.Sprintf("%sUsage: %s <username>%s", Red, binaryName, Reset)
}

func (v *View) FormatError(errorMsg string) string {
	return fmt.Sprintf("%s%s%s", Red, errorMsg, Reset)
}

func (v *View) FormatWarn(warnMsg string) string {
	return fmt.Sprintf("%s%s%s", Bold, warnMsg, Reset)
}

func (v *View) FormatTemplate(msg string, repoName string, date time.Time) string {
	// "color_date date color msg color_reset "
	out := "%s(%s)%s %s%s"
	outDate := date.Format(time.DateTime)
	msg = fmt.Sprintf(msg, repoName)
	return fmt.Sprintf(out, Green, outDate, (Reset + Cyan), msg, Reset)
}

func (v *View) FormatActivities(username string, activities []UserActivity) string {
	var sb strings.Builder

	title := v.FormatWarn(fmt.Sprintf("Recent activity of %s", username))
	sb.WriteString(title)

	for _, activity := range activities {
		template, repoName, date := activity.GetInfo()
		sb.WriteString(Bold)
		sb.WriteString("\n+ ")
		sb.WriteString(v.FormatTemplate(template, repoName, date))
	}
	return sb.String()
}
