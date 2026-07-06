package org.duckdns.massemiso.weather_api.weather;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeatherControllerTest {

  @Mock
  private WeatherService weatherService;

  @InjectMocks
  private WeatherController weatherController;

  @Test
  void getTodayForecast_ShouldCallService() {
    // arrange
    String cityCode = "London";
    String unit = "metric";
    Weather mockWeather = new Weather();
    when(weatherService.getForecast(any())).thenReturn(mockWeather);

    // act
    weatherController.getTodayForecast(cityCode, unit);

    // assert
    ArgumentCaptor<WeatherQuery> queryCaptor = ArgumentCaptor.forClass(WeatherQuery.class);
    verify(weatherService).getForecast(queryCaptor.capture());
    
    WeatherQuery query = queryCaptor.getValue();
    assertNotNull(query);
    assert(query.cityCode().equals(cityCode));
  }

  @Test
  void getWeekForecast_ShouldCallService() {
    // arrange
    String cityCode = "London";
    String unit = "metric";
    Weather mockWeather = new Weather();
    when(weatherService.getForecast(any())).thenReturn(mockWeather);

    // act
    weatherController.getWeekForecast(cityCode, unit);

    // assert
    ArgumentCaptor<WeatherQuery> queryCaptor = ArgumentCaptor.forClass(WeatherQuery.class);
    verify(weatherService).getForecast(queryCaptor.capture());
    
    WeatherQuery query = queryCaptor.getValue();
    assertNotNull(query);
    assert(query.cityCode().equals(cityCode));
  }
}
