package org.duckdns.massemiso.expense_tracker_api.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss") LocalDateTime timestamp,
    Integer status,
    String message,
    List<String> details
) {
}
