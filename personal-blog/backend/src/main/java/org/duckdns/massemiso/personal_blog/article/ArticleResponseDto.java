package org.duckdns.massemiso.personal_blog.article;

import java.time.LocalDateTime;

public record ArticleResponseDto(
    Long id, String title, String content, LocalDateTime dateOfPublication) {}
