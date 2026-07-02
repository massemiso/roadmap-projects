package org.duckdns.massemiso.weather_api.weather;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class WeatherService {

  @Value("${visualcrossing.api.key}")
  private String apiKey;
  @Value("${visualcrossing.url}")
  private String url;
  @Autowired
  private RestClient.Builder restClientBuilder;

  public Weather getForecastToday(String cityCode, DataUnit unit) {
    log.info("Getting today forecast for '{}'", cityCode);
    RestClient client = restClientBuilder.baseUrl(url).build();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String date1 = LocalDate.now().format(formatter);

    Weather dto = client
        .get()
        .uri("/{cityCode}/{date1}?key={apiKey}&contentType=json&unitGroup={unit}",
            cityCode, date1, apiKey, unit.toString().toLowerCase())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(Weather.class);
    log.info("Returning weather forecast today for '{}'", cityCode);
    return dto;
  }

  public Weather getForecastWeek(String cityCode, DataUnit unit) {
    log.info("Getting week forecast for '{}'", cityCode);
    RestClient client = restClientBuilder.baseUrl(url).build();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String date1 = LocalDate.now().format(formatter);
    String date2 = LocalDate.now().plusDays(7).format(formatter);

    Weather dto = client
        .get()
        .uri("/{cityCode}/{date1}/{date2}?key={apiKey}&contentType=json&unitGroup={unit}",
            cityCode, date1, date2, apiKey, unit.toString().toLowerCase())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(Weather.class);
    log.info("Returning weather forecast week for '{}'", cityCode);
    return dto;
  }
}
