package org.duckdns.massemiso.expense_tracker_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import org.duckdns.massemiso.expense_tracker_api.model.Category;

@Schema(description = "Expense request dto")
public record ExpenseRequestDto(
    @Schema(example = "New computer")
    @NotEmpty(message = "must not be empty") String description,

    @Schema(example = "ELECTRONICS")
    @NotNull(message = "must be not empty and a valid category") Category category,

    @NotNull(message = "must not be null")
    @PastOrPresent(message = "must not be in a future date")
    LocalDate date
) { }
