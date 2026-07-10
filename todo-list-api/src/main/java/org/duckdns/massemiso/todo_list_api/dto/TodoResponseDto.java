package org.duckdns.massemiso.todo_list_api.dto;

public record TodoResponseDto (
    Long id,
    String title,
    String description,
    Boolean completed
) { }
