package org.duckdns.massemiso.weather_api.weather;

import java.time.LocalDate;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
@Slf4j
public class WeatherController {

  @Autowired
  private WeatherService weatherService;

  @GetMapping("/{cityCode}/today")
  public Weather getTodayForecast(
      @PathVariable String cityCode,
      @RequestParam(required = false, defaultValue = "metric") String unit){
    log.info("Getting today forecast for '{}-{}'", cityCode, unit.toLowerCase());
    DataUnit dataUnit = DataUnit.valueOf(unit.toUpperCase());
    WeatherQuery query = new WeatherQuery(cityCode, Optional.of(LocalDate.now()), Optional.empty(), dataUnit);
    return weatherService.getForecast(query);
  }

  @GetMapping("/{cityCode}/week")
  public Weather getWeekForecast(
      @PathVariable String cityCode,
      @RequestParam(required = false, defaultValue = "metric") String unit){
    log.info("Getting week forecast for '{}-{}'", cityCode, unit.toLowerCase());
    DataUnit dataUnit = DataUnit.valueOf(unit.toUpperCase());
    WeatherQuery query = new WeatherQuery(cityCode, Optional.of(LocalDate.now()),
          Optional.of(LocalDate.now().plusDays(7)), dataUnit);
    return weatherService.getForecast(query);
  }
}
