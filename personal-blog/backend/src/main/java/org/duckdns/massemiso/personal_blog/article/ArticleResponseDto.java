package org.duckdns.massemiso.personal_blog.article;

import java.time.LocalDate;

public record ArticleResponseDto(Long id, String title, String content, LocalDate dop) {}
