package org.duckdns.massemiso.todo_list_api.dto;

public record AuthResponseDto(
    String accessToken,
    String refreshToken
) { }
