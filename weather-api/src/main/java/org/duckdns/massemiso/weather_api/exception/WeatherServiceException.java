package org.duckdns.massemiso.weather_api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class WeatherServiceException extends RuntimeException {
  private final HttpStatusCode statusCode;
  public WeatherServiceException(HttpStatusCode statusCode) {
    super("External API failed with status: " + statusCode);
    this.statusCode = statusCode;
  }
}
