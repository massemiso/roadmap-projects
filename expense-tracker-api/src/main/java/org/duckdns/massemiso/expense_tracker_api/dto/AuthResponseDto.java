package org.duckdns.massemiso.expense_tracker_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO for authentication")
public record AuthResponseDto(
    @Schema(description = "JWT token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String token
) {}
