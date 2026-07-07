package org.duckdns.massemiso.blogging_platform_api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PostRequestDto(
   @NotEmpty(message = "must not be empty") String title,
   @NotEmpty(message = "must not be empty") String content,
   @NotEmpty(message = "must not be empty") String category,
   @NotNull @Size(min = 1, message = "need to have at least 1 tag") List<String> tags
) {}
