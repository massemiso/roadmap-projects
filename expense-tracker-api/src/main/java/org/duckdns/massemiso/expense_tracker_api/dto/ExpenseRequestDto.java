package org.duckdns.massemiso.expense_tracker_api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import org.duckdns.massemiso.expense_tracker_api.model.Category;

public record ExpenseRequestDto(
    @NotEmpty String description,
    @NotNull Category category,
    @PastOrPresent LocalDate date
) { }
