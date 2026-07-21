package org.duckdns.massemiso.expense_tracker_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.cglib.core.Local;

@Schema(description = "Expense response dto")
public record ExpenseResponseDto(
    @Schema(example = "1")
    Long id,

    @Schema(example = "New computer")
    String description,

    @Schema(example = "ELECTRONICS")
    String category,

    @Schema(example = "2026-07-21")
    LocalDate date,

    Instant createdAt,
    Instant lastModifiedAt
) {
}
