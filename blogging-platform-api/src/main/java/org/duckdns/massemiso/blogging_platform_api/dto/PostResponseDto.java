package org.duckdns.massemiso.blogging_platform_api.dto;

import java.util.List;

public record PostResponseDto(
    Long id,
    String title,
    String content,
    String category,
    List<String> tags,
    String createdAt,
    String updatedAt
) {}
