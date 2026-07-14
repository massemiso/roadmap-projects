package org.duckdns.massemiso.expense_tracker_api;

import org.springframework.boot.SpringApplication;

public class TestExpenseTrackerApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(ExpenseTrackerApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
