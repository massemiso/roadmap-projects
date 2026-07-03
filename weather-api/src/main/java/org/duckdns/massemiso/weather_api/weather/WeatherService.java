package org.duckdns.massemiso.weather_api.weather;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
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
  private final RestClient.Builder restClientBuilder;

  @Autowired
  public WeatherService(RestClient.Builder restClientBuilder) {
    this.restClientBuilder = restClientBuilder;
  }

  @Cacheable(key = "#weatherQuery.makeCacheKey()", value = "WEATHER_CACHE")
  public Weather getForecast(WeatherQuery weatherQuery) {
    log.info("Getting forecast for '{}'", weatherQuery.cityCode());

    RestClient client = restClientBuilder.baseUrl(url).build();
    Weather dto = client
        .get()
        .uri(uriBuilder -> weatherQuery.makeQuery(uriBuilder, apiKey))
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(Weather.class);

    log.info("Returning forecast for '{}'", weatherQuery.cityCode());
    return dto;
  }
}
