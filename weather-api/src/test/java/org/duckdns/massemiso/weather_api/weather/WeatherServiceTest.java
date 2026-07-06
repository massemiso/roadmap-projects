package org.duckdns.massemiso.weather_api.weather;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.function.Function;
import org.duckdns.massemiso.weather_api.exception.WeatherServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;


@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

  @Mock
  RestClient.Builder restClientBuilder;

  @Mock
  RestClient restClient;

  @Mock
  RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

  @Mock
  RestClient.RequestHeadersSpec requestHeadersSpec;

  @Mock
  RestClient.ResponseSpec responseSpec;

  @InjectMocks
  WeatherService weatherService;

  private final String url = "https://example.com/weather";
  private final String apiKey = "KEY_123456";

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(weatherService, "url", url);
    ReflectionTestUtils.setField(weatherService, "apiKey", apiKey);
  }

  @Test
  void getForecast_GivenValidWeatherQuery_ShouldReturnWeatherResponse() {
    // arrange
    WeatherQuery query = new WeatherQuery("London", Optional.empty(), Optional.empty(), DataUnit.METRIC);
    Weather expectedWeather = new Weather();
    expectedWeather.setAddress("London");

    when(restClientBuilder.baseUrl(anyString())).thenReturn(restClientBuilder);
    when(restClientBuilder.build()).thenReturn(restClient);

    when(restClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.accept(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.body(Weather.class)).thenReturn(expectedWeather);

    // act
    Weather result = weatherService.getForecast(query);

    // assert
    assertNotNull(result);
    assertEquals("London", result.getAddress());

    verify(restClientBuilder).baseUrl(anyString());
    verify(restClientBuilder).build();
    verify(restClient).get();
    verify(requestHeadersUriSpec).uri(any(Function.class));
    verify(requestHeadersSpec).accept(any());
    verify(requestHeadersSpec).retrieve();
    verify(responseSpec).onStatus(any(), any());
    verify(responseSpec).body(Weather.class);
  }

  @Test
  void getForecast_GivenApiError_ShouldThrowWeatherServiceException() {
    // arrange
    WeatherQuery query = new WeatherQuery("London", Optional.empty(), Optional.empty(), DataUnit.METRIC);

    when(restClientBuilder.baseUrl(anyString())).thenReturn(restClientBuilder);
    when(restClientBuilder.build()).thenReturn(restClient);

    when(restClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.accept(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    
    // Simulate error status
    when(responseSpec.onStatus(any(), any())).thenAnswer(invocation -> {
      throw new WeatherServiceException(HttpStatus.NOT_FOUND);
    });

    // act & assert
    assertThrows(WeatherServiceException.class, () -> weatherService.getForecast(query));

    verify(restClientBuilder).baseUrl(anyString());
    verify(restClientBuilder).build();
    verify(restClient).get();
    verify(requestHeadersUriSpec).uri(any(Function.class));
    verify(requestHeadersSpec).accept(any());
    verify(requestHeadersSpec).retrieve();
    verify(responseSpec).onStatus(any(), any());
    verify(responseSpec, never()).body(Weather.class);
  }
}
