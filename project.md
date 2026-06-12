# Expense Tracker

[Back to Projects](https://roadmap.sh/backend/projects)
[Project DetailDetails](https://roadmap.sh/projects/expense-tracker)
[Community SolutionsSolutions](https://roadmap.sh/projects/expense-tracker/solutions)

Build a simple expense tracker to manage your finances.

Build a simple expense tracker application to manage your finances. The application should allow users to add, delete, and view their expenses. The application should also provide a summary of the expenses.

## Requirements

Application should run from the command line and should have the following features:

- Users can add an expense with a description and amount.
- Users can update an expense.
- Users can delete an expense.
- Users can view all expenses.
- Users can view a summary of all expenses.
- Users can view a summary of expenses for a specific month (of current year).

Here are some additional features that you can add to the application:

- Add expense categories and allow users to filter expenses by category.
- Allow users to set a budget for each month and show a warning when the user exceeds the budget.
- Allow users to export expenses to a CSV file.

The list of commands and their expected output is shown below:

```bash
$ expense-tracker add --description "Lunch" --amount 20
# Expense added successfully (ID: 1)

$ expense-tracker add --description "Dinner" --amount 10
# Expense added successfully (ID: 2)

$ expense-tracker list
# ID  Date       Description  Amount
# 1   2024-08-06  Lunch        $20
# 2   2024-08-06  Dinner       $10

$ expense-tracker summary
# Total expenses: $30

$ expense-tracker delete --id 2
# Expense deleted successfully

$ expense-tracker summary
# Total expenses: $20

$ expense-tracker summary --month 8
# Total expenses for August: $20
```

## Implementation

You can implement the application using any programming language of your choice. Here are some suggestions:

- Use any programming language for any available module for parsing command arguments (e.g. python with the `argparse`, node.js with `commander` etc).
- Use a simple text file to store the expenses data. You can use JSON, CSV, or any other format to store the data.
- Add error handling to handle invalid inputs and edge cases (e.g. negative amounts, non-existent expense IDs, etc).
- Use functions to modularize the code and make it easier to test and maintain.

---

This project idea is a great way to practice your logic building skills and learn how to interact with the filesystem using a CLI application. It will also help you understand how to manage data and provide useful information to users in a structured way.

Join the Community

roadmap.sh is the [6th most starred project on GitHub](https://github.com/search?o=desc&q=stars%3A%3E100000&s=stars&type=Repositories) and is visited by hundreds of thousands of developers every month.

[Roadmaps](https://roadmap.sh/roadmaps)
[Guides](https://roadmap.sh/guides)
[FAQs](https://roadmap.sh/about)
[YouTube](https://youtube.com/theroadmap?sub_confirmation=1)

[![ThewNewStack](https://roadmap.sh/img/tns-sm.png)](https://thenewstack.io/)

The top DevOps resource for Kubernetes, cloud-native computing, and large-scale development and deployment.
