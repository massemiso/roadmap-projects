package org.duckdns.massemiso.personal_blog.article;

import java.time.LocalDate;

public record ArticleRequestDto(String title, String content, LocalDate dateOfPublication) {}
