package org.duckdns.massemiso.blogging_platform_api.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse (
    LocalDateTime timestamp,
    int status,
    String message,
    List<String> details
)
{}
