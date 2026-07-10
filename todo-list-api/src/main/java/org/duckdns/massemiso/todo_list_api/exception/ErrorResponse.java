package org.duckdns.massemiso.todo_list_api.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    LocalDateTime timestamp,
    Integer status,
    String message,
    List<String> details
) { }
