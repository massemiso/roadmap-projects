package org.duckdns.massemiso.weather_api.weather;

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
    log.info("Getting today forecast for '{}'", cityCode);
    DataUnit dataUnit = DataUnit.valueOf(unit.toUpperCase());
    return weatherService.getForecastToday(cityCode, dataUnit);
  }

  @GetMapping("/{cityCode}/week")
  public Weather getWeekForecast(
      @PathVariable String cityCode,
      @RequestParam(required = false, defaultValue = "metric") String unit){
    log.info("Getting week forecast for '{}'", cityCode);
    DataUnit dataUnit = DataUnit.valueOf(unit.toUpperCase());
    return weatherService.getForecastWeek(cityCode, dataUnit);
  }
}
