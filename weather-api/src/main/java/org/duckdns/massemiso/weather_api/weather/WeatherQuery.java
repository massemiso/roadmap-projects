package org.duckdns.massemiso.weather_api.weather;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.springframework.web.util.UriBuilder;

public record WeatherQuery(
    String cityCode,
    Optional<LocalDate> date1,
    Optional<LocalDate> date2,
    DataUnit dataUnit
) {

  public URI makeQuery(UriBuilder uriBuilder, String apiKey) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    StringBuilder builder = new StringBuilder();

    builder.append("/").append(cityCode);
    if (date1.isPresent()) {
      builder.append("/").append(date1.get().format(formatter));
      date2.ifPresent(localDate -> builder.append("/").append(localDate.format(formatter)));
    }

    return uriBuilder
        .path(builder.toString())
        .queryParam("key", apiKey)
        .queryParam("unitGroup", dataUnit.toString().toLowerCase())
        .queryParam("contentType", "json")
        .build();
  }

  public String makeCacheKey() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    StringBuilder builder = new StringBuilder();

    builder.append(cityCode);
    if (date1.isPresent()) {
      builder.append("_").append(date1.get().format(formatter));
      date2.ifPresent(localDate -> builder.append("_").append(localDate.format(formatter)));
    }
    builder.append("_").append(dataUnit.toString().toLowerCase());

    return builder.toString();
  }
}
