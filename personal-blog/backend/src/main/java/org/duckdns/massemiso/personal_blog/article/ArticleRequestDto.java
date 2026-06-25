package org.duckdns.massemiso.personal_blog.article;

import java.time.LocalDateTime;

public record ArticleRequestDto(String title, String content, LocalDateTime dateOfPublication) {}
