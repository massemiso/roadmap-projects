package org.duckdns.massemiso.weather_api.weather;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Weather{
  Long id;
  String address;
  String timezone;
  String description;
  List<WeatherDay> days;
}
