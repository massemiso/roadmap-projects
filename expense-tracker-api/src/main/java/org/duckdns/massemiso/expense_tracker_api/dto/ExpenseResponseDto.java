package org.duckdns.massemiso.expense_tracker_api.dto;

import java.time.Instant;
import java.time.LocalDate;

public record ExpenseResponseDto(
    Long id,
    String description,
    String category,
    LocalDate date,
    Instant createdAt,
    Instant lastModifiedAt
) {
}
