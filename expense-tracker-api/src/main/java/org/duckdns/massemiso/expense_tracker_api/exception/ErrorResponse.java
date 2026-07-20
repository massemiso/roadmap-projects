package org.duckdns.massemiso.expense_tracker_api.exception;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public record ErrorResponse(
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm") LocalDateTime timestamp,
    Integer status,
    String message,
    List<String> details
) {
}
