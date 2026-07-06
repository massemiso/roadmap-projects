package org.duckdns.massemiso.weather_api.weather;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

class WeatherQueryTest {

  private final UriBuilder uriBuilder = new DefaultUriBuilderFactory().builder();

  @Test
  void makeQuery_GivenValidApiKeyAndDate1_ReturnURI() {
    WeatherQuery query = new WeatherQuery("London",
        Optional.of(LocalDate.of(2025, 1, 1)),
        Optional.empty(),
        DataUnit.METRIC);
    URI uri = query.makeQuery(uriBuilder, "secret-key");

    assertThat(uri.toString()).contains("/London/2025-01-01");
    assertThat(uri.toString()).contains("key=secret-key");
    assertThat(uri.toString()).contains("unitGroup=metric");
  }

  @Test
  void makeQuery_GivenValidApiKeyAndDate1AndDate2_ReturnURI() {
    WeatherQuery query = new WeatherQuery("London",
        Optional.of(LocalDate.of(2025, 1, 1)),
        Optional.of(LocalDate.of(2025, 1, 7)),

        DataUnit.METRIC);
    URI uri = query.makeQuery(uriBuilder, "secret-key");

    assertThat(uri.toString()).contains("/London/2025-01-01/2025-01-07");
    assertThat(uri.toString()).contains("key=secret-key");
  }

  @Test
  void makeCacheKey_ReturnsExpectedFormat() {
    WeatherQuery query = new WeatherQuery("London",
        Optional.of(LocalDate.of(2025, 1, 1)),
        Optional.empty(),
        DataUnit.METRIC);
    String key = query.makeCacheKey();

    assertThat(key).isEqualTo("London_2025-01-01_metric");
  }
}

