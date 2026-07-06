package org.duckdns.massemiso.blogging_platform_api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PostRequestDto(
   @NotEmpty String title,
   @NotEmpty String content,
   @NotEmpty String category,
   @NotNull @Size(min = 1) List<String> tags
) {}
